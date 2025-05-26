package com.app.krankmanagement.repository

import com.app.krankmanagement.datamodel.UserProfile
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    private val _userState = MutableStateFlow<UserProfile?>(null)
    val userState: StateFlow<UserProfile?> get() = _userState

    fun listenUserData(uid: String) {
        usersRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserProfile::class.java)
                _userState.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                _userState.value = null
            }
        })
    }

    fun updateUserData(uid: String, userProfile: UserProfile) {
        usersRef.child(uid).setValue(userProfile)
    }
}

