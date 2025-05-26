//package com.app.krankmanagement.viewModel
//
//import android.util.Log
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import com.app.krankmanagement.datamodel.UserProfile
//import com.app.krankmanagement.repository.AuthRepository
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.firestore
//import kotlinx.coroutines.flow.StateFlow
//
//class AuthViewModel : ViewModel() {
//    private val auth = FirebaseAuth.getInstance()
//    private val db = Firebase.firestore
//
//    val currentUser = mutableStateOf<UserProfile?>(null)
//    val loading = mutableStateOf(false)
//    val error = mutableStateOf<String?>(null)
//
//    private val authRepository = AuthRepository()
//
//    val user: StateFlow<UserProfile?> = authRepository.userState
//
//    fun loadUser(uid: String) {
//        authRepository.listenUserData(uid)
//    }
//
//    fun updateUser(userProfile: UserProfile) {
//        auth.currentUser?.uid?.let { uid ->
//            authRepository.updateUserData(uid, userProfile)
//        }
//    }
//    fun register(email: String, password: String, role: String) {
//        loading.value = true
//        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
//            val uid = it.user?.uid ?: return@addOnSuccessListener
//            val userProfile = UserProfile(uid, email, role)
//            currentUser.value = userProfile
//            loading.value = false
//        }.addOnFailureListener {
//            Log.d("register: ", "addOnFailureListener")
//            error.value = it.localizedMessage
//            loading.value = false
//        }
//    }
//
//    fun login(email: String, password: String) {
//        loading.value = true
//        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
//            val firebaseUser = authResult.user
//            if (firebaseUser != null) {
//
//                val user = UserProfile(
//                    uid = firebaseUser.uid,
//                    email = firebaseUser.email ?: "",
//                )
//                currentUser.value = user
//                Log.d("login", "User email: ${user.email}")
//            } else {
//                error.value = "User not found"
//                currentUser.value = null
//            }
//            loading.value = false
//        }.addOnFailureListener {
//            error.value = it.localizedMessage
//            loading.value = false
//        }
//    }
//
//
//
////    fun login(email: String, password: String) {
////        loading.value = true
////        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
////            val uid = it.user?.uid ?: return@addOnSuccessListener
////            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
////                val user = doc.toObject(UserProfile::class.java)
////                Log.d("login: ",user?.email ?: "not found")
////                currentUser.value = user
////                loading.value = false
////            }
////        }.addOnFailureListener {
////            error.value = it.localizedMessage
////            loading.value = false
////        }
////    }
//
//    fun logout() {
//        auth.signOut()
//        currentUser.value = null
//    }
//}


package com.app.krankmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.UserProfile
import com.app.krankmanagement.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser = mutableStateOf<UserProfile?>(null)
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    private val authRepository = AuthRepository()
    val user: StateFlow<UserProfile?> = authRepository.userState
    fun register(email: String, password: String, role: String) {
        loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    // Get FCM token and save user data + token to Firestore
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result ?: ""
                            val userProfile = UserProfile(
                                uid = firebaseUser.uid,
                                email = email,
                                role = role,
                                fcmToken = token
                            )
                            // Save user data to Firestore
                            db.collection("users")
                                .document(firebaseUser.uid)
                                .set(userProfile, SetOptions.merge())
                                .addOnSuccessListener {
                                    currentUser.value = userProfile
                                    loading.value = false
                                }
                                .addOnFailureListener { e ->
                                    error.value = e.localizedMessage
                                    loading.value = false
                                }
                        } else {
                            error.value = task.exception?.localizedMessage ?: "Failed to get token"
                            loading.value = false
                        }
                    }
                } else {
                    error.value = "User registration failed"
                    loading.value = false
                }
            }
            .addOnFailureListener { e ->
                error.value = e.localizedMessage
                loading.value = false
            }
    }

    fun login(email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    // Fetch user info from Firestore
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { documentSnapshot ->
                            val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                            if (userProfile != null) {
                                // Update FCM token on login
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val token = task.result ?: ""
                                        db.collection("users").document(uid)
                                            .update("fcmToken", token)
                                            .addOnSuccessListener {
                                                currentUser.value = userProfile.copy(fcmToken = token)
                                                loading.value = false
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("AuthViewModel", "Failed to update token: ${e.localizedMessage}")
                                                currentUser.value = userProfile
                                                loading.value = false
                                            }
                                    } else {
                                        currentUser.value = userProfile
                                        loading.value = false
                                    }
                                }
                            } else {
                                // User document doesn't exist in Firestore, create minimal data
                                val userProfile = UserProfile(
                                    uid = uid,
                                    email = email,
                                    role = if (email == "ali@gmail.com") "manager" else "employee"
                                )
                                currentUser.value = userProfile
                                loading.value = false
                            }
                        }
                        .addOnFailureListener { e ->
                            error.value = e.localizedMessage
                            loading.value = false
                        }
                } else {
                    error.value = "User not found"
                    currentUser.value = null
                    loading.value = false
                }
            }
            .addOnFailureListener { e ->
                error.value = e.localizedMessage
                loading.value = false
            }
    }

    fun logout() {
        auth.signOut()
        currentUser.value = null
    }
}
