package com.app.krankmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.SickLeaveRequest
import com.app.krankmanagement.datamodel.TakeOverShift
import com.app.krankmanagement.repository.SickLeaveRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore

class SickLeaveViewModel : ViewModel() {

    private val repository = SickLeaveRepository()

    val leaveRequests = mutableStateListOf<SickLeaveRequest>()
    val leaveAllRequests = mutableStateListOf<SickLeaveRequest>()
    val takeoverLeave = mutableStateListOf<TakeOverShift>()
    val allTakeoverLeave = mutableStateListOf<TakeOverShift>()
    val email = mutableStateListOf("")
    val loading = mutableStateOf(false)

    fun submitLeave(from: String, to: String, userId: String) {
        loading.value = true
        val request = SickLeaveRequest(
            uid = userId,
            fromDate = from,
            toDate = to,
            status = "pending"
        )
        repository.submitLeave(request)
        loading.value = false
    }


    fun loadUserLeaves(userId: String) {
        loading.value = true
        repository.loadUserLeaves(userId) { leaves ->
            leaveRequests.clear()
            leaveRequests.addAll(leaves)
            loading.value = false
        }
    }
    fun loadAllAllTakeOver() {
        loading.value = true
        repository.getAllTakeoverLeavesFromFirebase { leaves ->
            allTakeoverLeave.clear()
            allTakeoverLeave.addAll(leaves)
            loading.value = false
        }
    }
    fun loadAllTakeoverLeaves() {
        loading.value = true
        repository.loadAllTakeoverLeaves { leaves ->
            takeoverLeave.clear()
            takeoverLeave.addAll(leaves)
            loading.value = false
        }
    }
    fun loadAllUserLeaves() {
        loading.value = true
        repository.loadAllLeaves { leaves ->
            leaveAllRequests.clear()
            leaveAllRequests.addAll(leaves)
            loading.value = false
        }
    }


    fun getuserDetail(userId:String):String{
        var emailActual = ""
        repository.getEmailForUserId(userId) { getEmail ->
            email.addAll(listOf(getEmail.toString()))
        }
        return emailActual
    }
    fun updateTakeOverStatus(uid: String, newStatus: String) {
        loading.value = true

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("takeOver").child(uid).child("status")
        ref.setValue(newStatus)
            .addOnSuccessListener {
                loadAllTakeoverLeaves()
                loading.value = false
            }
            .addOnFailureListener {
                loadAllTakeoverLeaves()
                Log.e("Firebase", "Failed to update status", it)
                loading.value = false
            }
    }

    fun updateLeaveStatus(leaveId: String, newStatus: String) {
        loading.value = true

        val ref = FirebaseDatabase.getInstance().getReference("sickLeaves")
            .child(leaveId)
            .child("status")

        ref.setValue(newStatus)
            .addOnSuccessListener {
                loadAllUserLeaves()
                loading.value = false
            }
            .addOnFailureListener {
                loadAllUserLeaves()
                Log.e("Firebase", "Failed to update status", it)
                loading.value = false
            }
    }
}
