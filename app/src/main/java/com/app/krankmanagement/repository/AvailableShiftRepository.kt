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

class AvailableShiftRepository {

    private val database = FirebaseDatabase.getInstance()
    private val sickLeavesRef = database.getReference("sickLeaves")

    val ref = FirebaseDatabase.getInstance().getReference("takeOver")

    // For state observation in UI
    private val _sickUserState = MutableStateFlow<SickLeaveRequest?>(null)
    val sickUserState: StateFlow<SickLeaveRequest?> get() = _sickUserState

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

    fun sendTakeOverShift(shift: TakeOverShift, onComplete: (Boolean) -> Unit) {
        val key = shift.uid
        ref.child(key).setValue(shift)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}

