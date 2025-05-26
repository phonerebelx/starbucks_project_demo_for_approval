package com.app.krankmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.UserProfile
import com.app.krankmanagement.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val uid = it.user?.uid ?: return@addOnSuccessListener
            val userProfile = UserProfile(uid, email, role)
            currentUser.value = userProfile
            loading.value = false
        }.addOnFailureListener {
            Log.d("register: ", "addOnFailureListener")
            error.value = it.localizedMessage
            loading.value = false
        }
    }

    fun login(email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val firebaseUser = authResult.user
            if (firebaseUser != null) {

                val user = UserProfile(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                )
                currentUser.value = user
                Log.d("login", "User email: ${user.email}")
            } else {
                error.value = "User not found"
                currentUser.value = null
            }
            loading.value = false
        }.addOnFailureListener {
            error.value = it.localizedMessage
            loading.value = false
        }
    }



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
