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


    private val _sickUserState = MutableStateFlow<SickLeaveRequest?>(null)
    val sickUserState: StateFlow<SickLeaveRequest?> get() = _sickUserState

    fun loadAllLeaves(onResult: (List<SickLeaveRequest>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val sickLeavesRef = database.getReference("sickLeaves")
        val usersRef = database.getReference("users")

        sickLeavesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(sickLeavesSnapshot: DataSnapshot) {
                val leaves = mutableListOf<SickLeaveRequest>()
                val userEmailMap = mutableMapOf<String, String>()

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(usersSnapshot: DataSnapshot) {

                        for (userSnapshot in usersSnapshot.children) {
                            val uid = userSnapshot.key
                            val email = userSnapshot.child("email").getValue(String::class.java)
                            if (uid != null && email != null) {
                                userEmailMap[uid] = email
                            }
                        }


                        for (leaveSnapshot in sickLeavesSnapshot.children) {
                            val leave = leaveSnapshot.getValue(SickLeaveRequest::class.java)
                            if (leave != null) {
                                leave.id = leaveSnapshot.key ?: ""
                                leave.mail = userEmailMap[leave.uid] ?: ""
                                leaves.add(leave)
                            }
                        }

                        onResult(leaves)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onResult(emptyList())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }



//    fun loadAllLeaves(onResult: (List<SickLeaveRequest>) -> Unit) {
//        sickLeavesRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val leaves = mutableListOf<SickLeaveRequest>()
//                for (childSnapshot in snapshot.children) {
//                    val leave = childSnapshot.getValue(SickLeaveRequest::class.java)
//                    if (leave != null) leaves.add(leave)
//                }
//                onResult(leaves)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onResult(emptyList())
//            }
//        })
//    }

    fun sendTakeOverShift(shift: TakeOverShift, onComplete: (Boolean) -> Unit) {
        val newRef = ref.push() // generates a unique key every time
        shift.uid = newRef.key ?: "" // assign generated key to shift.uid if needed
        newRef.setValue(shift)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

}

