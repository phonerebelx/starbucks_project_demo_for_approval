package com.app.krankmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.UserProfile
import com.app.krankmanagement.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    val currentUser = mutableStateOf<UserProfile?>(null)
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    private val authRepository = AuthRepository()

    val user: StateFlow<UserProfile?> = authRepository.userState

    fun loadUser(uid: String) {
        authRepository.listenUserData(uid)
    }

    fun updateUser(userProfile: UserProfile) {
        auth.currentUser?.uid?.let { uid ->
            authRepository.updateUserData(uid, userProfile)
        }
    }

    fun register(email: String, password: String, role: String) {

        loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    var userProfile = UserProfile(uid, email, role, token)
                    if (email == "ali@gmail.com") {
                        userProfile.role = "manager"
                    }
                    // Save user to Realtime Database
                    val dbRef = FirebaseDatabase.getInstance().reference
                    dbRef.child("users").child(uid).setValue(userProfile)
                        .addOnSuccessListener {
                            currentUser.value = userProfile
                            loading.value = false
                        }
                        .addOnFailureListener {
                            error.value = it.localizedMessage
                            loading.value = false
                        }
                }.addOnFailureListener {
                    error.value = it.localizedMessage
                    loading.value = false
                }
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
                loading.value = false
            }
    }


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
    fun login(email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val uid = firebaseUser.uid
                val dbRef = FirebaseDatabase.getInstance().reference

                // 🟢 Fetch user data first
                dbRef.child("users").child(uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingUser = snapshot.getValue(UserProfile::class.java)
                        if (existingUser != null) {
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                val updatedUser = existingUser.copy(token = token)
                                // 🟢 Update token only
                                dbRef.child("users").child(uid).setValue(updatedUser)
                                currentUser.value = updatedUser
                                loading.value = false
                            }.addOnFailureListener {
                                error.value = "Token fetch failed: ${it.localizedMessage}"
                                loading.value = false
                            }
                        } else {
                            error.value = "User profile not found"
                            loading.value = false
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        this@AuthViewModel.error.value = "Database error: ${error.message}"
                        loading.value = false
                    }
                })
            } else {
                error.value = "User not found"
                loading.value = false
            }
        }.addOnFailureListener {
            error.value = it.localizedMessage
            loading.value = false
        }
    }
    fun clearUser() {
        currentUser.value = null
    }

//    fun login(email: String, password: String) {
//        loading.value = true
//        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
//            val firebaseUser = authResult.user
//            if (firebaseUser != null) {
//                val uid = firebaseUser.uid
//                val user = UserProfile(
//                    uid = uid,
//                    email = firebaseUser.email ?: ""
//                )
//
//                // Update the token
//                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
//                    val updatedUser = user.copy(token = token)
//                    FirebaseDatabase.getInstance().reference
//                        .child("users")
//                        .child(uid)
//                        .setValue(updatedUser)
//                    currentUser.value = updatedUser
//                    loading.value = false
//                }.addOnFailureListener {
//                    error.value = "Token fetch failed: ${it.localizedMessage}"
//                    loading.value = false
//                }
//            } else {
//                error.value = "User not found"
//                loading.value = false
//            }
//        }.addOnFailureListener {
//            error.value = it.localizedMessage
//            loading.value = false
//        }
//    }


//    fun login(email: String, password: String) {
//        loading.value = true
//        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
//            val uid = it.user?.uid ?: return@addOnSuccessListener
//            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
//                val user = doc.toObject(UserProfile::class.java)
//                Log.d("login: ",user?.email ?: "not found")
//                currentUser.value = user
//                loading.value = false
//            }
//        }.addOnFailureListener {
//            error.value = it.localizedMessage
//            loading.value = false
//        }
//    }

    fun logout() {
        auth.signOut()
        currentUser.value = null
    }
}
