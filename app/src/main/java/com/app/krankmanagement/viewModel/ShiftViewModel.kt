package com.app.krankmanagement.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.SickLeaveRequest
import com.app.krankmanagement.datamodel.TakeOverShift
import com.app.krankmanagement.repository.AvailableShiftRepository
import com.app.krankmanagement.repository.SickLeaveRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ShiftViewModel : ViewModel() {
    private val db = Firebase.firestore
    val openShifts = mutableStateListOf<TakeOverShift>()
    private val repository = AvailableShiftRepository()

    val leaveRequests = mutableStateListOf<SickLeaveRequest>()
    val loading = mutableStateOf(false)
    fun loadUserLeaves() {
        loading.value = true
        repository.loadAllLeaves() { leaves ->
            leaveRequests.clear()
            leaveRequests.addAll(leaves)
            loading.value = false
        }
    }


    fun sendTakeOverShift(shift: TakeOverShift, onComplete: (Boolean) -> Unit) {
        repository.sendTakeOverShift(shift, onComplete)
    }
//    fun requestShift(shiftId: String, userId: String) {
//        db.collection("shifts").document(shiftId)
//            .update(mapOf(
//                "takenBy" to userId,
//                "status" to "taken",
//                "managerApproved" to null
//            ))
//    }
}
