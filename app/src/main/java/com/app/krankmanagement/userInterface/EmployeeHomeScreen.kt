package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EmployeeHomeScreen(userId: String) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Leave", "Available Shifts")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTab) {
            0 -> SickLeaveScreen(viewModel = viewModel(), userId = userId)
            1 -> OpenShiftsScreen(viewModel = viewModel(), currentUserId = userId)
        }
    }
}
