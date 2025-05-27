package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.krankmanagement.viewModel.SickLeaveViewModel

@Composable
fun PendingTakeOverRequest(modifier: Modifier = Modifier,viewModel: SickLeaveViewModel) {
    LaunchedEffect(true) {
        viewModel.loadAllTakeoverLeaves()

    }
    Spacer(modifier = Modifier.height(30.dp))

    Column (modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally){


        Text("All Take over Leave", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(30.dp))
        if (viewModel.loading.value) {
            CircularProgressIndicator(color = Color(0xFF00704A), modifier = Modifier.fillMaxWidth())
        } else {
            LazyColumn {
                items(viewModel.takeoverLeave) { leave ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )


                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Employee Mail: ${leave.mail}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "From: ${leave.fromDate}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "To: ${leave.toDate}", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Status: ${leave.status.capitalize()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            if(leave.status == "Accepted" ||leave.status == "Rejected"){

                            }else{
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.updateTakeOverStatus(leave.uid,"Accepted")
                                        },
                                        shape = RoundedCornerShape(50), // Pill shape
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Green
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Text(text = "Accept", color = Color.White)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.updateTakeOverStatus(leave.uid,"Rejected")
                                        },
                                        shape = RoundedCornerShape(50), // Pill shape
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // Red
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Text(text = "Reject", color = Color.White)
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