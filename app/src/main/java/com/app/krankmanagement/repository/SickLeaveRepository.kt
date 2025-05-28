package com.app.krankmanagement.repository

import android.util.Log
import com.app.krankmanagement.datamodel.SickLeaveRequest
import com.app.krankmanagement.datamodel.TakeOverShift
import com.app.krankmanagement.datamodel.UserProfile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
        val newKey = sickLeavesRef.push().key ?: return
        sickLeavesRef.child(newKey).setValue(request)
            .addOnSuccessListener {
                _sickUserState.value = request
            }
            .addOnFailureListener {
                _sickUserState.value = null
            }
    }
    fun getAllTakeoverLeavesFromFirebase(
        onResult: (List<TakeOverShift>) -> Unit
    ) {
        database.getReference("takeOver").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val takeOverLeaves = mutableListOf<TakeOverShift>()

                for (child in snapshot.children) {
                    val shift = child.getValue(TakeOverShift::class.java)
                    if (shift != null && shift.uid != shift.originalUserId) {
                        takeOverLeaves.add(shift)
                    }
                }

                onResult(takeOverLeaves)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
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
//
//                val leaves = mutableListOf<SickLeaveRequest>()
//                for (childSnapshot in snapshot.children) {
//                    val leave = childSnapshot.getValue(SickLeaveRequest::class.java)
//                    if (leave != null) {
//                        leave.id = childSnapshot.key.orEmpty()
//                        leaves.add(leave)
//                    }
//                }
//                onResult(leaves)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                onResult(emptyList())
//            }
//        })
//    }

//    fun loadAllTakeoverLeaves(onResult: (List<TakeOverShift>) -> Unit) {
//        val database = FirebaseDatabase.getInstance()
//        val takeOverRef = database.getReference("takeOver")
//
//        takeOverRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val list = mutableListOf<TakeOverShift>()
//                for (child in snapshot.children) {
//                    val request = child.getValue(TakeOverShift::class.java)
//                    request?.let { list.add(it) }
//                }
//                onResult(list)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
//    }

    fun getEmailForUserId(userId: String, onResult: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child("email").getValue(String::class.java)
                onResult(email)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }


    fun loadAllTakeoverLeaves(onResult: (List<TakeOverShift>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val takeOverRef = database.getReference("takeOver")
        val usersRef = database.getReference("users")

        takeOverRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(takeOverSnapshot: DataSnapshot) {
                val list = mutableListOf<TakeOverShift>()
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

                        for (child in takeOverSnapshot.children) {
                            val request = child.getValue(TakeOverShift::class.java)
                            if (request != null) {
                                request.uid = child.key ?: ""
                                request.mail = userEmailMap[request.originalUserId] ?: ""
                                list.add(request)
                            }
                        }

                        onResult(list)
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

}

