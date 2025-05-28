package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.app.krankmanagement.viewModel.ManagerViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ManagerHomeScreen(viewModel: ManagerViewModel, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf("Pending Sick Leave Request", "Pending Shift Takeover Requests")

            Column {
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
                    0 -> SickLeaveRequest(viewModel = viewModel())
                    1 -> PendingTakeOverRequest(viewModel = viewModel())
                }
            }
        }

        // Floating Action Button
//        FloatingActionButton(
//            onClick = {
//                FirebaseAuth.getInstance().signOut()
//                navController.navigate("onboarding"){
//                    popUpTo(0) { inclusive = true }
//                    launchSingleTop = true
//                }
//            },
//            containerColor = Color(0xFF00796B),
//            contentColor = Color.White,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp)
//        ) {
//            Icon(Icons.Default.ExitToApp, contentDescription = "Exit")
//        }
    }

    LaunchedEffect(true) {
        viewModel.loadPendingRequests()
    }
}
