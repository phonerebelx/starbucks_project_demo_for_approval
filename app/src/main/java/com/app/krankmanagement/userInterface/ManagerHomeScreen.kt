package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
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
import com.app.krankmanagement.viewModel.ManagerViewModel

@Composable
fun ManagerHomeScreen(viewModel: ManagerViewModel) {
    Column() {

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
                        modifier = Modifier.height(85.dp), // custom height
                        text = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = title,Modifier.padding(top = 18.dp))
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

//        LazyColumn {
//            items(viewModel.pendingLeaves) { leave ->
//                Card(modifier = Modifier.padding(vertical = 4.dp)) {
//                    Column(Modifier.padding(8.dp)) {
//                        Text("Employee: ${leave.userId}")
//                        Text("From: ${leave.fromDate}")
//                        Text("To: ${leave.toDate}")
//                        Row {
//                            Button(onClick = { viewModel.approveLeave(leave) }) {
//                                Text("Approve")
//                            }
//                            Spacer(Modifier.width(8.dp))
//                            Button(onClick = { viewModel.rejectLeave(leave) }) {
//                                Text("Reject")
//                            }
//                        }
//                    }
//                }
//            }
//        }


//        LazyColumn {
//            items(viewModel.pendingShifts) { shift ->
//                Card(modifier = Modifier.padding(vertical = 4.dp)) {
//                    Column(Modifier.padding(8.dp)) {
//                        Text("Original: ${shift.originalUserId}")
//                        Text("Requested By: ${shift.takenBy}")
//                        Text("From: ${shift.fromDate}")
//                        Text("To: ${shift.toDate}")
//                        Row {
//                            Button(onClick = { viewModel.approveShift(shift) }) {
//                                Text("Approve")
//                            }
//                            Spacer(Modifier.width(8.dp))
//                            Button(onClick = { viewModel.rejectShift(shift) }) {
//                                Text("Reject")
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    LaunchedEffect(true) {
        viewModel.loadPendingRequests()
    }
}
