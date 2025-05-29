package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.krankmanagement.viewModel.AuthViewModel

@Composable
fun EmployeeHomeScreen(viewmodel: AuthViewModel,userId: String,navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Leave", "Available Shifts","Takeover")
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFE0F2F1),
                contentColor = Color(0xFF00796B),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF00796B),
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.height(85.dp),
                        text = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = title, Modifier.padding(top = 18.dp))
                            }
                        }
                    )
                }
            }

            Divider(color = Color(0xFF00704A), thickness = 1.dp)

            when (selectedTab) {
                0 -> SickLeaveScreen(viewModel = viewModel(), userId = userId)
                1 -> OpenShiftsScreen(viewModel = viewModel(), currentUserId = userId)
                2-> CurrentTakeover(viewModel = viewModel())
            }
        }
        FloatingActionButton(
            onClick = {
                viewmodel.Logout(context)
                navController.navigate("onboarding") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            containerColor = Color(0xFF00796B),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Exit")
        }



    }
}

