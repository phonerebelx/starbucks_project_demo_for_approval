
package com.app.krankmanagement.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.MainActivity
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
    val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val dbRef = FirebaseDatabase.getInstance().reference

    val currentUser = mutableStateOf<UserProfile?>(null)
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    private val authRepository = AuthRepository()

    val user: StateFlow<UserProfile?> = authRepository.userState


    fun register(email: String, password: String, role: String) {
        loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                updateFcmToken(uid) { token ->
                    val assignedRole = if (email == "ali@gmail.com") "manager" else role
                    val userProfile = UserProfile(uid, email, assignedRole, token)
                    dbRef.child("users").child(uid).setValue(userProfile)
                        .addOnSuccessListener {
                            currentUser.value = userProfile
                            loading.value = false
                        }
                        .addOnFailureListener {
                            error.value = it.localizedMessage
                            loading.value = false
                        }
                }
            }
            .addOnFailureListener {
                error.value = it.localizedMessage
                loading.value = false
            }
    }

    private fun updateFcmToken(uid: String, onComplete: (String) -> Unit = {}) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                dbRef.child("users").child(uid).child("fcmToken").setValue(token)
                    .addOnSuccessListener { onComplete(token) }
                    .addOnFailureListener {
                        error.value = "FCM token update failed: ${it.localizedMessage}"
                    }
            }
            .addOnFailureListener {
                error.value = "Could not fetch FCM token: ${it.localizedMessage}"
            }
    }


    fun login(email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val uid = firebaseUser.uid
                val dbRef = FirebaseDatabase.getInstance().reference

                //  Fetch user data first
                dbRef.child("users").child(uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val existingUser = snapshot.getValue(UserProfile::class.java)
                        if (existingUser != null) {
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                val updatedUser = existingUser.copy(token = token)
                                //  Update token only
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


    fun checkIfUserIsLoggedIn(context: Context): Boolean {
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val uid = firebaseUser.uid
            val db = FirebaseDatabase.getInstance().reference
            db.child("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(UserProfile::class.java)
                    currentUser.value = user
                    updateFcmToken(uid)
                }
                .addOnFailureListener {
                    Logout(context)
                }
            Log.d("checkIfUser", "FirebaseUser: ${currentUser.value}")
            return true
        } else {
            currentUser.value = null
            return false
        }
    }

    fun restartApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.overridePendingTransition(0, 0) // No animation
            context.finish()
        }
    }


    fun Logout(context: Context) {
        Log.d("Logout", "Logging out...")
        auth.signOut()
        restartApp(context)

        currentUser.value = null

        Log.d("Logout", "CurrentUser after signout: ${auth.currentUser}")
    }
}
//package com.app.krankmanagement.viewModel


//
//import android.util.Log
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import com.app.krankmanagement.datamodel.UserProfile
//import com.app.krankmanagement.repository.AuthRepository
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.firestore.firestore
//import com.google.firebase.messaging.FirebaseMessaging
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
//
//    fun register(email: String, password: String, role: String) {
//
//        loading.value = true
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener { authResult ->
//                val uid = authResult.user?.uid ?: return@addOnSuccessListener
//                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
//                    var userProfile = UserProfile(uid, email, role, token)
//                    if (email == "ali@gmail.com") {
//                        userProfile.role = "manager"
//                    }
//                    // Save user to Realtime Database
//                    val dbRef = FirebaseDatabase.getInstance().reference
//                    dbRef.child("users").child(uid).setValue(userProfile)
//                        .addOnSuccessListener {
//                            currentUser.value = userProfile
//                            loading.value = false
//                        }
//                        .addOnFailureListener {
//                            error.value = it.localizedMessage
//                            loading.value = false
//                        }
//                }.addOnFailureListener {
//                    error.value = it.localizedMessage
//                    loading.value = false
//                }
//            }
//            .addOnFailureListener {
//                error.value = it.localizedMessage
//                loading.value = false
//            }
//    }
//
//
//    //    fun register(email: String, password: String, role: String) {
////        loading.value = true
////        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
////            val uid = it.user?.uid ?: return@addOnSuccessListener
////            val userProfile = UserProfile(uid, email, role)
////            currentUser.value = userProfile
////            loading.value = false
////        }.addOnFailureListener {
////            Log.d("register: ", "addOnFailureListener")
////            error.value = it.localizedMessage
////            loading.value = false
////        }
////    }
//    fun login(email: String, password: String) {
//        loading.value = true
//        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
//            val firebaseUser = authResult.user
//            if (firebaseUser != null) {
//                val uid = firebaseUser.uid
//                val dbRef = FirebaseDatabase.getInstance().reference
//
//                // 🟢 Fetch user data first
//                dbRef.child("users").child(uid).addListenerForSingleValueEvent(object :
//                    ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val existingUser = snapshot.getValue(UserProfile::class.java)
//                        if (existingUser != null) {
//                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
//                                val updatedUser = existingUser.copy(token = token)
//                                // 🟢 Update token only
//                                dbRef.child("users").child(uid).setValue(updatedUser)
//                                currentUser.value = updatedUser
//                                loading.value = false
//                            }.addOnFailureListener {
//                                error.value = "Token fetch failed: ${it.localizedMessage}"
//                                loading.value = false
//                            }
//                        } else {
//                            error.value = "User profile not found"
//                            loading.value = false
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        this@AuthViewModel.error.value = "Database error: ${error.message}"
//                        loading.value = false
//                    }
//                })
//            } else {
//                error.value = "User not found"
//                loading.value = false
//            }
//        }.addOnFailureListener {
//            error.value = it.localizedMessage
//            loading.value = false
//        }
//    }
//    fun clearUser() {
//        currentUser.value = null
//    }
//
////    fun login(email: String, password: String) {
////        loading.value = true
////        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
////            val firebaseUser = authResult.user
////            if (firebaseUser != null) {
////                val uid = firebaseUser.uid
////                val user = UserProfile(
////                    uid = uid,
////                    email = firebaseUser.email ?: ""
////                )
////
////                // Update the token
////                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
////                    val updatedUser = user.copy(token = token)
////                    FirebaseDatabase.getInstance().reference
////                        .child("users")
////                        .child(uid)
////                        .setValue(updatedUser)
////                    currentUser.value = updatedUser
////                    loading.value = false
////                }.addOnFailureListener {
////                    error.value = "Token fetch failed: ${it.localizedMessage}"
////                    loading.value = false
////                }
////            } else {
////                error.value = "User not found"
////                loading.value = false
////            }
////        }.addOnFailureListener {
////            error.value = it.localizedMessage
////            loading.value = false
////        }
////    }
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
//
//
//    fun logout() {
//        auth.signOut()
//        currentUser.value = null
//    }
//}
