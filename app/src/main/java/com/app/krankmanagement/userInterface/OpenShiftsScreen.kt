package com.app.krankmanagement.userInterface

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.krankmanagement.R
import com.app.krankmanagement.datamodel.TakeOverShift
import com.app.krankmanagement.viewModel.ShiftViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@Composable
fun OpenShiftsScreen(viewModel: ShiftViewModel, currentUserId: String) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("TakeOverPrefs", Context.MODE_PRIVATE)

    val isRefreshing = viewModel.loading.value
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            "All Sick Leave Requests",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.loadUserLeaves() }
        ) {
            if (viewModel.leaveRequests.isEmpty() && !isRefreshing) {
                Text("No sick leave requests found.")
            } else {
                LazyColumn {
                    items(viewModel.leaveRequests) { leave ->
                        if (leave.uid != currentUserId) {
                            val leaveKey = "${leave.uid}_${leave.fromDate}_${leave.toDate}"
                            var showButton by remember(leaveKey) {
                                mutableStateOf(!sharedPref.getBoolean(leaveKey, false))
                            }
                            var isLoading by remember { mutableStateOf(false) }

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                                            contentDescription = "User Icon",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(Color(0xFF00704A), Color(0xFF4CAF50))
                                                    )
                                                ),
                                            tint = Color.White
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = leave.mail ?: "Unknown",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Leave Status: ${leave.status?.capitalize() ?: "N/A"}",
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "From: ${leave.fromDate ?: "N/A"}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "To: ${leave.toDate ?: "N/A"}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    if (showButton && leave.status == "Accepted") {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                val firebaseAuth = FirebaseAuth.getInstance()
                                                val database = FirebaseDatabase.getInstance().reference

                                                val currentUserUid = firebaseAuth.currentUser?.uid

                                                isLoading = true
                                                val takeOverShift = TakeOverShift(
                                                    uid = currentUserId,
                                                    originalUserId = leave.uid ?: "",
                                                    fromDate = leave.fromDate ?: "",
                                                    toDate = leave.toDate ?: "",
                                                    status = "open",
                                                    takenBy = currentUserUid,
                                                    managerApproved = null
                                                )

                                                viewModel.sendTakeOverShift(takeOverShift) { success ->
                                                    isLoading = false
                                                    Toast.makeText(
                                                        context,
                                                        if (success) "Request sent!" else "Failed to send request.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    if (success) {
                                                        showButton = false
                                                        sharedPref.edit().putBoolean(leaveKey, true).apply()
                                                    }
                                                }
                                            },
                                            enabled = !isLoading,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF00704A),
                                                disabledContainerColor = Color(0xFF00704A),
                                                contentColor = Color.White,
                                                disabledContentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                        ) {
                                            if (isLoading) {
                                                CircularProgressIndicator(
                                                    color = Color.White,
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text("Apply")
                                            }
                                        }
                                    }
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

//package com.app.krankmanagement.userInterface
//import android.widget.Toast
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ElevatedCard
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.app.krankmanagement.datamodel.TakeOverShift
//import com.app.krankmanagement.viewModel.ShiftViewModel
//
//@Composable
//
//fun OpenShiftsScreen(viewModel: ShiftViewModel, currentUserId: String) {
//    val context = LocalContext.current
//
//    Column(modifier = Modifier.padding(16.dp)) {
////        Text("Available Shifts", style = MaterialTheme.typography.headlineSmall)
//
////        if (viewModel.openShifts.isEmpty()) {
////            Text("No open shifts currently.")
////        } else {
////            LazyColumn {
////                items(viewModel.openShifts) { shift ->
////                    Card(modifier = Modifier.padding(vertical = 6.dp)) {
////                        Column(Modifier.padding(8.dp)) {
////                            Text("From: ${shift.fromDate}")
////                            Text("To: ${shift.toDate}")
////                            Button(onClick = {
////                                viewModel.requestShift(shift.uid, currentUserId)
////                                Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show()
////                            }) {
////                                Text("Request This Shift")
////                            }
////                        }
////                    }
////                }
////            }
////        }
//
//        // Show Sick Leave Requests below
//        Text(
//            "All Sick Leave Requests",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(top = 16.dp)
//        )
//
//        if (viewModel.leaveRequests.isEmpty()) {
//            Text("No sick leave requests found.")
//        } else {
//            LazyColumn {
//                items(viewModel.leaveRequests) { leave ->
//                    if (leave.uid != currentUserId){
//                    ElevatedCard(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = Color.White
//                        )
//                    ) {
//                        Column(Modifier.padding(8.dp)) {
//                            Spacer(modifier = Modifier.padding(vertical = 2.dp))
//                            Text("User Mail: ${leave.mail ?: "Unknown"}")
//                            Spacer(modifier = Modifier.padding(vertical = 4.dp))
//                            Text("From: ${leave.fromDate ?: "N/A"}")
//                            Spacer(modifier = Modifier.padding(vertical = 4.dp))
//                            Text("To: ${leave.toDate ?: "N/A"}")
//                        }
//                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
//
//                        // Button aligned to the right
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//
//                                ) {
//                                Button(
//                                    onClick = {
//                                        val takeOverShift = TakeOverShift(
//                                            uid = currentUserId,
//                                            originalUserId = leave.uid ?: "",
//                                            fromDate = leave.fromDate ?: "",
//                                            toDate = leave.toDate ?: "",
//                                            status = "open",
//                                            takenBy = null,
//                                            managerApproved = null
//                                        )
//
//                                        viewModel.sendTakeOverShift(takeOverShift) { success ->
//                                            Toast.makeText(
//                                                context,
//                                                if (success) "Request sent!" else "Failed to send request.",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                    },
//                                    enabled = !viewModel.loading.value,
//                                    colors = ButtonDefaults.buttonColors(
//                                        containerColor = Color(0x9504A36D),
//                                        contentColor = Color.White,
//                                        disabledContentColor = Color.White
//                                    ), modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(50.dp)
//
//                                ) {
//                                    Text("Apply")
//                                }
//                            }
//                        }
//
//
//                    }
//                }
//            }
//        }
//    }
//
//    LaunchedEffect(true) {
//        viewModel.loadUserLeaves()
//    }
//}
