package com.app.krankmanagement.userInterface

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.krankmanagement.viewModel.SickLeaveViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun SickLeaveScreen(viewModel: SickLeaveViewModel, userId: String) {
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }

    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Submit Sick Leave",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

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
                    // Reload the data after submission
                    viewModel.loadUserLeaves(userId)
                    // Clear the selected dates
                    fromDate = null
                    toDate = null
                    Toast.makeText(context, "Request submitted", Toast.LENGTH_SHORT).show()
                }
            },

            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00704A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(
                    width = 2.dp,
                    color = Color(0xFF00704A),
                    shape = RoundedCornerShape(12.dp)
                ),

            enabled = fromDate != null && toDate != null
        ) {
            Text("Submit")
        }

        Divider(Modifier.padding(vertical = 16.dp))

        Text("Your Sick Leave Requests", style = MaterialTheme.typography.titleMedium)
        if (viewModel.loading.value) {
            CircularProgressIndicator(color = Color(0xFF00704A))
        } else {
            LazyColumn {
                items(viewModel.leaveRequests) { leave ->
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
                                "From: ${leave.fromDate}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("To: ${leave.toDate}", fontSize = 14.sp,fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Status: ${leave.status.capitalize()}", fontSize = 14.sp,fontWeight = FontWeight.Bold,
                            )
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

@Composable
fun DatePickerBox(label: String, selectedDate: LocalDate?, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current

    // Format the date for display
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val displayText = selectedDate?.format(dateFormatter) ?: ""

    // Get current date or selected date for the picker
    val calendar = Calendar.getInstance()
    val year = selectedDate?.year ?: calendar.get(Calendar.YEAR)
    val month = (selectedDate?.monthValue?.minus(1)) ?: calendar.get(Calendar.MONTH)
    val day = selectedDate?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)

    Box {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )

        // Invisible clickable overlay that captures all touch events
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            println("DatePicker clicked for: $label") // Debug print
                            try {
                                DatePickerDialog(
                                    context,
                                    { _, pickedYear, pickedMonth, pickedDay ->
                                        println("Date selected: $pickedYear-${pickedMonth + 1}-$pickedDay")
                                        onDateSelected(
                                            LocalDate.of(
                                                pickedYear,
                                                pickedMonth + 1,
                                                pickedDay
                                            )
                                        )
                                    },
                                    year, month, day
                                ).show()
                            } catch (e: Exception) {
                                println("Error showing DatePicker: ${e.message}")
                            }
                        }
                    )
                }
        )
    }
}