package com.app.krankmanagement

import android.app.Application
import com.google.firebase.FirebaseApp

class ShiftBuddyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}