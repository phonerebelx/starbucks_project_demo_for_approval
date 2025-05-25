package com.app.krankmanagement.userInterface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.datamodel.UserProfile
import com.app.krankmanagement.viewModel.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel, onAuthSuccess: (UserProfile) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("employee") }

    if (viewModel.currentUser.value != null) {
        onAuthSuccess(viewModel.currentUser.value!!)
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = if (isRegister) "Register" else "Login", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        if (isRegister) {
            DropdownMenuBox(role, onRoleChange = { role = it })
        }

        Button(
            onClick = {
                if (isRegister) viewModel.register(email, password, role)
                else viewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(if (isRegister) "Register" else "Login")
        }

        TextButton(onClick = { isRegister = !isRegister }) {
            Text(if (isRegister) "Already have an account? Login" else "Don't have an account? Register")
        }

        viewModel.error.value?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
