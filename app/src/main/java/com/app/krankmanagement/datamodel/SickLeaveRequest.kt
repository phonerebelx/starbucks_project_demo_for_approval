package com.app.krankmanagement.datamodel


data class SickLeaveRequest(
    val id: String = "",
    val userId: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val status: String = "pending"
)
