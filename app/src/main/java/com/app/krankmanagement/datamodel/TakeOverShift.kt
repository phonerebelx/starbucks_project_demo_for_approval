package com.app.krankmanagement.datamodel

data class TakeOverShift(
    val uid: String = "",
    val originalUserId: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val status: String = "open",
    val takenBy: String? = null,
    val managerApproved: Boolean? = null
)
