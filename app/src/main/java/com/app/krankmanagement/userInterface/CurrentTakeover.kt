package com.app.krankmanagement.userInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.krankmanagement.viewModel.SickLeaveViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
    fun CurrentTakeover(modifier: Modifier = Modifier,viewModel: SickLeaveViewModel) {
    LaunchedEffect(true) {
        viewModel.loadAllAllTakeOver()
    }
    Spacer(modifier = Modifier.height(30.dp))

    val isRefreshing = viewModel.loading.value
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("All Take Over Requests", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(30.dp))

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.loadAllAllTakeOver()
            }
        ) {
            if (isRefreshing) {
                // Optionally keep showing a progress indicator while refreshing
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00704A))
                }
            } else {
                LazyColumn {

                    items(viewModel.allTakeoverLeave) { leave ->
                        viewModel.getuserDetail(leave.originalUserId)
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Taken By: ${leave.mail ?: "Unknown"}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "From: ${leave.fromDate ?: "N/A"}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "To: ${leave.toDate ?: "N/A"}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status: ",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = leave.status?.replaceFirstChar { it.uppercaseChar() } ?: "N/A",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (leave.status?.lowercase()) {
                                            "accepted" -> Color(0xFF4CAF50)
                                            "rejected" -> Color(0xFFF44336)
                                            "pending" -> Color(0xFFFF9800)
                                            else -> Color.Gray
                                        },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                color = when (leave.status?.lowercase()) {
                                                    "accepted" -> Color(0x334CAF50)
                                                    "rejected" -> Color(0x33F44336)
                                                    "pending" -> Color(0x33FF9800)
                                                    else -> Color(0x33000000)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}