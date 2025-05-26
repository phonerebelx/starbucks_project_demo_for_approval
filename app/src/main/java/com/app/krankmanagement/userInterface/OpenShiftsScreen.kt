package com.app.krankmanagement.userInterface

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.datamodel.TakeOverShift
import com.app.krankmanagement.viewModel.ShiftViewModel

@Composable

fun OpenShiftsScreen(viewModel: ShiftViewModel, currentUserId: String) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
//        Text("Available Shifts", style = MaterialTheme.typography.headlineSmall)

//        if (viewModel.openShifts.isEmpty()) {
//            Text("No open shifts currently.")
//        } else {
//            LazyColumn {
//                items(viewModel.openShifts) { shift ->
//                    Card(modifier = Modifier.padding(vertical = 6.dp)) {
//                        Column(Modifier.padding(8.dp)) {
//                            Text("From: ${shift.fromDate}")
//                            Text("To: ${shift.toDate}")
//                            Button(onClick = {
//                                viewModel.requestShift(shift.uid, currentUserId)
//                                Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show()
//                            }) {
//                                Text("Request This Shift")
//                            }
//                        }
//                    }
//                }
//            }
//        }

        // Show Sick Leave Requests below
        Text(
            "All Sick Leave Requests",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        if (viewModel.leaveRequests.isEmpty()) {
            Text("No sick leave requests found.")
        } else {
            LazyColumn {
                items(viewModel.leaveRequests) { leave ->
                    if (leave.uid != currentUserId){
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("User ID: ${leave.uid ?: "Unknown"}")
                            Text("From: ${leave.fromDate ?: "N/A"}")
                            Text("To: ${leave.toDate ?: "N/A"}")
                        }
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))

                        // Button aligned to the right
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Button(
                                onClick = {
                                    val takeOverShift = TakeOverShift(
                                        uid = currentUserId,
                                        originalUserId = leave.uid ?: "",
                                        fromDate = leave.fromDate ?: "",
                                        toDate = leave.toDate ?: "",
                                        status = "open",
                                        takenBy = null,
                                        managerApproved = null
                                    )

                                    viewModel.sendTakeOverShift(takeOverShift) { success ->
                                        Toast.makeText(
                                            context,
                                            if (success) "Request sent!" else "Failed to send request.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                enabled = !viewModel.loading.value,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00704A),
                                    disabledContainerColor = Color(0xFF00704A),
                                    contentColor = Color.White,
                                    disabledContentColor = Color.White
                                ), modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)

                            ) {
                                Text("Apply")
                            }
                        }
                    }
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.loadUserLeaves()
    }
}
