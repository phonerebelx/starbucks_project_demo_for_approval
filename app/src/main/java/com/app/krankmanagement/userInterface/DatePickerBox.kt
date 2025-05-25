import android.app.DatePickerDialog
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

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

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        println("DatePicker clicked!")
                        DatePickerDialog(
                            context,
                            { _, pickedYear, pickedMonth, pickedDay ->
                                onDateSelected(LocalDate.of(pickedYear, pickedMonth + 1, pickedDay))
                            },
                            year, month, day
                        ).show()
                    }
                )
            }
    )
}