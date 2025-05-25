package com.app.krankmanagement.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.SickLeaveRequest
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class SickLeaveViewModel : ViewModel() {
    private val db = Firebase.firestore
    val leaveRequests = mutableStateListOf<SickLeaveRequest>()
    val loading = mutableStateOf(false)

    fun submitLeave(from: String, to: String, userId: String) {
        val docId = db.collection("sickLeaves").document().id
        val request = SickLeaveRequest(
            id = docId,
            userId = userId,
            fromDate = from,
            toDate = to
        )

        db.collection("sickLeaves").document(docId).set(request)
    }


    fun loadUserLeaves(userId: String) {
        loading.value = true
        db.collection("sickLeaves")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                leaveRequests.clear()
                for (doc in result) {
                    leaveRequests.add(doc.toObject(SickLeaveRequest::class.java))
                }
                loading.value = false
            }
    }
}
