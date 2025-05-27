package com.app.krankmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
//import com.app.krankmanagement.datamodel.Shift
import com.app.krankmanagement.datamodel.SickLeaveRequest
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore

class ManagerViewModel : ViewModel() {
    private val db = Firebase.firestore
    val pendingLeaves = mutableStateListOf<SickLeaveRequest>()
//    val pendingShifts = mutableStateListOf<Shift>()


    fun loadPendingRequests() {
        // Load pending sick leaves
        db.collection("sickLeaves")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->
                pendingLeaves.clear()
                result.forEach { doc ->
                    pendingLeaves.add(doc.toObject(SickLeaveRequest::class.java))
                }
            }

        // Load pending shift takeovers
//        db.collection("shifts")
//            .whereEqualTo("status", "taken")
//            .whereEqualTo("managerApproved", null)
//            .get()
//            .addOnSuccessListener { result ->
//                pendingShifts.clear()
//                result.forEach { doc ->
//                    pendingShifts.add(doc.toObject(Shift::class.java))
//                }
//            }
    }

//    fun approveLeave(leave: SickLeaveRequest) {
//        db.collection("sickLeaves").document(leave.id)
//            .update("status", "approved")
//            .addOnSuccessListener {
//                // Create an open shift
//                val shiftId = db.collection("shifts").document().id
//                val shift = Shift(
//                    id = shiftId,
//                    originalUserId = leave.userId,
//                    fromDate = leave.fromDate,
//                    toDate = leave.toDate
//                )
//                db.collection("shifts").document(shiftId).set(shift)
//                loadPendingRequests()
//            }
//    }
//
//    fun rejectLeave(leave: SickLeaveRequest) {
//        db.collection("sickLeaves").document(leave.id)
//            .update("status", "rejected")
//            .addOnSuccessListener { loadPendingRequests() }
//    }

//    fun approveShift(shift: Shift) {
//        db.collection("shifts").document(shift.id)
//            .update("managerApproved", true)
//            .addOnSuccessListener { loadPendingRequests() }
//    }
//
//    fun rejectShift(shift: Shift) {
//        db.collection("shifts").document(shift.id)
//            .update(
//                mapOf(
//                    "managerApproved" to false,
//                    "status" to "open",
//                    "takenBy" to null
//                )
//            )
//            .addOnSuccessListener { loadPendingRequests() }
//    }
}
