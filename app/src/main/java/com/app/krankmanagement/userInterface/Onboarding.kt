package com.app.krankmanagement.userInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.krankmanagement.R

@Composable
fun StarbucksWelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00704A)) // Starbucks green
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.top_img), contentDescription = "Top Image", modifier = Modifier.size(296.dp))
        // Image Grid
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(3),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(240.dp),
//            horizontalArrangement = Arrangement.spacedBy(4.dp),
//            verticalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            items(6) {
//                Box(
//                    modifier = Modifier
//                        .aspectRatio(1f)
//                        .background(Color.LightGray, RoundedCornerShape(8.dp))
//                )
//            }
//        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to Starbucks",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Manage your Starbucks shifts with ease",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Continue with Login", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3932)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Register your Role", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By continuing, you agree to the Starbucks Terms & Conditions",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.8f)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

