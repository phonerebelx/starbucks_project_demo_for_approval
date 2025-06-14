package com.app.krankmanagement.userInterface

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.R
import com.app.krankmanagement.datamodel.UserProfile
import com.app.krankmanagement.viewModel.AuthViewModel


@Composable
fun AuthScreen(viewModel: AuthViewModel, isRegister: Boolean, onAuthSuccess: (UserProfile) -> Unit) {
    var email by remember { mutableStateOf("ali123@gmail.com") }
    var password by remember { mutableStateOf("123456") }
    var role by remember { mutableStateOf("employee") }


    val user by viewModel.user.collectAsState()



    if (viewModel.currentUser.value != null) {

        onAuthSuccess(viewModel.currentUser.value!!)
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.starbucks_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(128.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "It’s Go Time!",
                color = Color(0xFF00704A),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(100.dp))

            // White Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = if (isRegister) "Register" else "Login",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF00704A)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("User Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00704A),
                            unfocusedBorderColor = Color(0xFF00704A),
                            focusedLabelColor = Color(0xFF00704A),
                            unfocusedLabelColor = Color(0xFF00704A)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00704A),
                            unfocusedBorderColor = Color(0xFF00704A),
                            focusedLabelColor = Color(0xFF00704A),
                            unfocusedLabelColor = Color(0xFF00704A)

                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isRegister) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DropdownMenuBox(role, onRoleChange = { role = it })
                    }

                    Spacer(modifier = Modifier.height(8.dp))



                    Spacer(modifier = Modifier.height(16.dp))

//                    Button(
//                        onClick = {
//                            if (isRegister) viewModel.register(email, password, role)
//                            else viewModel.login(email, password)
//                        },
//                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00704A)),
//                        shape = RoundedCornerShape(12.dp),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp)
//                            .border(
//                                width = 2.dp,
//                                color = Color(0xFF00704A),
//                                shape = RoundedCornerShape(12.dp)
//                            )
//                    ) {
//                        Text(if (isRegister) "Register" else "Login", color = Color(0xFFFFFFFF))
//                    }

                    Button(
                        onClick = {
                            if (isRegister) viewModel.register(email, password, role)
                            else viewModel.login(email, password)
                        },
                        enabled = !viewModel.loading.value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00704A),
                            disabledContainerColor = Color(0xFF00704A),
                            contentColor = Color.White,
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFF00704A),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        if (viewModel.loading.value) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(if (isRegister) "Register" else "Login", color = Color(0xFFFFFFFF))
                        }
                    }



                    viewModel.error.value?.let {
                        Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}


//@Composable
//fun AuthScreen(viewModel: AuthViewModel,isRegister: Boolean, onAuthSuccess: (UserProfile) -> Unit) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
////    var isRegister by remember { mutableStateOf(false) }
//    var role by remember { mutableStateOf("employee") }
//
//    if (viewModel.currentUser.value != null) {
//        onAuthSuccess(viewModel.currentUser.value!!)
//        return
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(text = if (isRegister) "Register" else "Login", style = MaterialTheme.typography.headlineSmall)
//
//        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
//        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
//
//        if (isRegister) {
//            DropdownMenuBox(role, onRoleChange = { role = it })
//        }
//
//        Button(
//            onClick = {
//                if (isRegister) viewModel.register(email, password, role)
//                else viewModel.login(email, password)
//            },
//            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
//        ) {
//            Text(if (isRegister) "Register" else "Login")
//        }
//
////        TextButton(onClick = { isRegister = !isRegister }) {
////            Text(if (isRegister) "Already have an account? Login" else "Don't have an account? Register")
////        }
//
//        viewModel.error.value?.let {
//            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
//        }
//    }
//}
