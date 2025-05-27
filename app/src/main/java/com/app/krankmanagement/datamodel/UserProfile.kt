package com.app.krankmanagement.datamodel

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    var role: String = "employee", // or "manager"
    val token: String = ""
)
