package com.app.krankmanagement.datamodel

import com.google.firebase.Timestamp

data class TakeOverShift(
    var uid: String = "",
    var mail: String = "",
    val originalUserId: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val status: String = "open",
    val takenBy: String? = null,
    val managerApproved: Boolean? = null
)
