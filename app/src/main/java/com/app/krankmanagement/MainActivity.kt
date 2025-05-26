package com.app.krankmanagement

import EmployeeHomeScreen
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.krankmanagement.ui.theme.ShiftBuddyTheme
import com.app.krankmanagement.userInterface.AuthScreen

import com.app.krankmanagement.userInterface.ManagerHomeScreen
import com.app.krankmanagement.userInterface.StarbucksWelcomeScreen
import com.app.krankmanagement.viewModel.AuthViewModel
import com.app.krankmanagement.viewModel.ManagerViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            ShiftBuddyApp()
            }
        }


    @Composable
    fun ShiftBuddyApp() {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()

        NavHost(navController, startDestination = "onboarding") {
            composable("onboarding") {
                StarbucksWelcomeScreen(
                    onLoginClick = {
                        navController.navigate("auth?isRegistering=false") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("auth?isRegistering=true") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = "auth?isRegistering={isRegistering}",
                arguments = listOf(navArgument("isRegistering") {
                    type = NavType.BoolType
                    defaultValue = false
                })
            ) {
                val isRegistering = it.arguments?.getBoolean("isRegistering") ?: false
                AuthScreen(viewModel = authViewModel, isRegister = isRegistering) { user ->
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("ShiftBuddyApp: ",task.result)
                                val token = task.result
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(user.uid)
                                    .set(mapOf("fcmToken" to token), SetOptions.merge())

                            }
                        }

                    val isManager = user.email == "ali@gmail.com"
                    if (isManager) {
                        navController.navigate("managerHome") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        navController.navigate("employeeHome/${user.uid}") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            }

            composable("managerHome") {
                val viewModel: ManagerViewModel = viewModel()
                ManagerHomeScreen(viewModel = viewModel)
            }

            composable(
                "employeeHome/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                EmployeeHomeScreen(userId = userId)
            }
        }

    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "General Notifications"
            val descriptionText = "Channel for FCM notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("my_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}

