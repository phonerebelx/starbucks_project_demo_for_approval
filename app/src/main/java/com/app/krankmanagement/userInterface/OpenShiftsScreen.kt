package com.app.krankmanagement.userInterface

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.viewModel.ShiftViewModel

@Composable
fun OpenShiftsScreen(viewModel: ShiftViewModel, currentUserId: String) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Available Shifts", style = MaterialTheme.typography.headlineSmall)

        if (viewModel.openShifts.isEmpty()) {
            Text("No open shifts currently.")
        } else {
            LazyColumn {
                items(viewModel.openShifts) { shift ->
                    Card(modifier = Modifier.padding(vertical = 6.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text("From: ${shift.fromDate}")
                            Text("To: ${shift.toDate}")
                            Button(onClick = {
                                viewModel.requestShift(shift.id, currentUserId)
                                Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Request This Shift")
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.loadOpenShifts()
    }
}
