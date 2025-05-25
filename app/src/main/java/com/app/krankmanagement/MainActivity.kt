package com.app.krankmanagement

import android.os.Bundle
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
import com.app.krankmanagement.userInterface.EmployeeHomeScreen
import com.app.krankmanagement.userInterface.ManagerHomeScreen
import com.app.krankmanagement.viewModel.AuthViewModel
import com.app.krankmanagement.viewModel.ManagerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShiftBuddyApp()
            }
        }


    @Composable
    fun ShiftBuddyApp() {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()

        NavHost(navController, startDestination = "auth") {
            composable("auth") {
                AuthScreen(viewModel = authViewModel) { user ->
                    val route = if (user.role == "manager") "managerHome" else "employeeHome/${user.uid}"
                    navController.navigate(route) {
                        popUpTo("auth") { inclusive = true }
                    }
                }
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

