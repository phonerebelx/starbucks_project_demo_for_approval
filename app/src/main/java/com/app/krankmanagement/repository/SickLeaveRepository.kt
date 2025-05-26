package com.app.krankmanagement.repository

import com.app.krankmanagement.datamodel.SickLeaveRequest
import com.app.krankmanagement.datamodel.TakeOverShift
import com.app.krankmanagement.datamodel.UserProfile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SickLeaveRepository {

    private val database = FirebaseDatabase.getInstance()
    private val sickLeavesRef = database.getReference("sickLeaves")

    // For state observation in UI
    private val _sickUserState = MutableStateFlow<SickLeaveRequest?>(null)
    val sickUserState: StateFlow<SickLeaveRequest?> get() = _sickUserState

    fun submitLeave(request: SickLeaveRequest) {
        val key = request.uid
        sickLeavesRef.child(key).setValue(request)
            .addOnSuccessListener {
                _sickUserState.value = request
            }
            .addOnFailureListener {
                _sickUserState.value = null
            }
    }

    fun loadUserLeaves(userId: String, onResult: (List<SickLeaveRequest>) -> Unit) {

        sickLeavesRef.orderByChild("uid").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leaves = mutableListOf<SickLeaveRequest>()
                    for (childSnapshot in snapshot.children) {
                        val leave = childSnapshot.getValue(SickLeaveRequest::class.java)
                        if (leave != null) leaves.add(leave)
                    }
                    onResult(leaves)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun loadAllLeaves(onResult: (List<SickLeaveRequest>) -> Unit) {
        sickLeavesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaves = mutableListOf<SickLeaveRequest>()
                for (childSnapshot in snapshot.children) {
                    val leave = childSnapshot.getValue(SickLeaveRequest::class.java)
                    if (leave != null) leaves.add(leave)
                }
                onResult(leaves)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

    fun loadAllTakeoverLeaves(onResult: (List<TakeOverShift>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val takeOverRef = database.getReference("takeOver")

        takeOverRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TakeOverShift>()
                for (child in snapshot.children) {
                    val request = child.getValue(TakeOverShift::class.java)
                    request?.let { list.add(it) }
                }
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}

