import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.krankmanagement.userInterface.OpenShiftsScreen
import com.app.krankmanagement.userInterface.SickLeaveScreen

@Composable
fun EmployeeHomeScreen(userId: String) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Leave", "Available Shifts")

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
            0 -> SickLeaveScreen(viewModel = viewModel(), userId = userId)
            1 -> OpenShiftsScreen(viewModel = viewModel(), currentUserId = userId)
        }
    }
}
