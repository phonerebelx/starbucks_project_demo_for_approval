package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.viewModel.ManagerViewModel

@Composable
fun ManagerHomeScreen(viewModel: ManagerViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Pending Sick Leave Requests", style = MaterialTheme.typography.headlineSmall)

        LazyColumn {
            items(viewModel.pendingLeaves) { leave ->
                Card(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Employee: ${leave.userId}")
                        Text("From: ${leave.fromDate}")
                        Text("To: ${leave.toDate}")
                        Row {
                            Button(onClick = { viewModel.approveLeave(leave) }) {
                                Text("Approve")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { viewModel.rejectLeave(leave) }) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("Pending Shift Takeover Requests", style = MaterialTheme.typography.headlineSmall)

        LazyColumn {
            items(viewModel.pendingShifts) { shift ->
                Card(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Original: ${shift.originalUserId}")
                        Text("Requested By: ${shift.takenBy}")
                        Text("From: ${shift.fromDate}")
                        Text("To: ${shift.toDate}")
                        Row {
                            Button(onClick = { viewModel.approveShift(shift) }) {
                                Text("Approve")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { viewModel.rejectShift(shift) }) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.loadPendingRequests()
    }
}
