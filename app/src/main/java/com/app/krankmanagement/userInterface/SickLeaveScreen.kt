package com.app.krankmanagement.userInterface

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.viewModel.SickLeaveViewModel
import java.time.LocalDate
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.items
import java.time.format.DateTimeFormatter

@Composable
fun SickLeaveScreen(viewModel: SickLeaveViewModel, userId: String) {
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }

    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Submit Sick Leave", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))
        DatePickerBox("From Date", fromDate) { fromDate = it }
        DatePickerBox("To Date", toDate) { toDate = it }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (fromDate != null && toDate != null) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val fromDateStr = fromDate?.format(formatter) ?: ""
                    val toDateStr = toDate?.format(formatter) ?: ""
                    viewModel.submitLeave(fromDateStr, toDateStr, userId)
                    Toast.makeText(context, "Request submitted", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = fromDate != null && toDate != null
        ) {
            Text("Submit")
        }

        Divider(Modifier.padding(vertical = 16.dp))

        Text("Your Sick Leave Requests", style = MaterialTheme.typography.titleMedium)
        if (viewModel.loading.value) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(viewModel.leaveRequests) { leave ->
                    Card(modifier = Modifier.padding(vertical = 4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("From: ${leave.fromDate}")
                            Text("To: ${leave.toDate}")
                            Text("Status: ${leave.status.capitalize()}")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.loadUserLeaves(userId)
    }
}

