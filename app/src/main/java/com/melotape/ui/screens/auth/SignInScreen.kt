package com.melotape.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.melotape.ui.components.*
import com.melotape.ui.theme.*

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("alex@melotape.audio") }
    var password by remember { mutableStateOf("••••••••") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        MelotapeTopBar(
            title = "Sign In",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "MELOTAPE DECK",
                style = MaterialTheme.typography.labelSmall,
                color = Amber,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
            )

            Text(
                text = "Sign in to synchronize your tape vaults & cloud mixtapes",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = SurfaceElevated,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                ),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Master Key (Password)", color = TextSecondary) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = SurfaceElevated,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                ),
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onSignInSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
            ) {
                Text("Sign In to Pro Deck", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSignInSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
            ) {
                Text("Continue as Guest / Offline Deck", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }
        }
    }
}
