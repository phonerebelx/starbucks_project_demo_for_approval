package com.app.krankmanagement
import android.Manifest
import EmployeeHomeScreen
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
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

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // You can show UI here explaining why permission is needed before requesting
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly request the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }


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

}

