package com.app.krankmanagement.datamodel


data class SickLeaveRequest(
    var id: String = "",
    var mail: String = "",
    val uid: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val status: String = "pending"
)
