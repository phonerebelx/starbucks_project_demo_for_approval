package com.app.krankmanagement.datamodel

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val role: String = "employee", // or "manager"
    val fcmToken: String = ""
)
