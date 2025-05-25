package com.app.krankmanagement.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.app.krankmanagement.datamodel.Shift
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ShiftViewModel : ViewModel() {
    private val db = Firebase.firestore
    val openShifts = mutableStateListOf<Shift>()

    fun loadOpenShifts() {
        db.collection("shifts")
            .whereEqualTo("status", "open")
            .get()
            .addOnSuccessListener { result ->
                openShifts.clear()
                for (doc in result) {
                    openShifts.add(doc.toObject(Shift::class.java))
                }
            }
    }

    fun requestShift(shiftId: String, userId: String) {
        db.collection("shifts").document(shiftId)
            .update(mapOf(
                "takenBy" to userId,
                "status" to "taken",
                "managerApproved" to null
            ))
    }
}
