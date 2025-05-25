package com.app.krankmanagement.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    val currentUser = mutableStateOf<UserProfile?>(null)
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun register(email: String, password: String, role: String) {
        loading.value = true
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val uid = it.user?.uid ?: return@addOnSuccessListener
            val userProfile = UserProfile(uid, email, role)
            db.collection("users").document(uid).set(userProfile).addOnSuccessListener {
                currentUser.value = userProfile
                loading.value = false
            }
        }.addOnFailureListener {
            error.value = it.localizedMessage
            loading.value = false
        }
    }

    fun login(email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            val uid = it.user?.uid ?: return@addOnSuccessListener
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                val user = doc.toObject(UserProfile::class.java)
                currentUser.value = user
                loading.value = false
            }
        }.addOnFailureListener {
            error.value = it.localizedMessage
            loading.value = false
        }
    }

    fun logout() {
        auth.signOut()
        currentUser.value = null
    }
}
