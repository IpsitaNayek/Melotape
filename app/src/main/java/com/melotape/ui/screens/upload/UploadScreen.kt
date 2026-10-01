package com.melotape.ui.screens.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.melotape.ui.components.*
import com.melotape.ui.theme.*

@Composable
fun UploadScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf("Analog Tape Session 01") }
    var artist by remember { mutableStateOf("Alex & Friends") }
    var album by remember { mutableStateOf("Home Tapes 2024") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
    ) {
        MelotapeTopBar(
            title = "Upload & Tape Rip",
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

        Column(modifier = Modifier.padding(16.dp)) {
            DashedDropZone(
                title = "Select Master Audio File",
                subtitle = "Supports FLAC 24-bit/96kHz, WAV, MP3",
                buttonText = "Browse Storage",
                onSelectFile = {
                    title = "Otari MX50 Analog Session"
                    artist = "Alex Solo"
                },
            )

            Spacer(Modifier.height(20.dp))

            Text("TRACK METADATA", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Track Title", color = TextSecondary) },
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

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = artist,
                onValueChange = { artist = it },
                label = { Text("Artist", color = TextSecondary) },
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

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = album,
                onValueChange = { album = it },
                label = { Text("Tape Album", color = TextSecondary) },
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

            Spacer(Modifier.height(20.dp))

            if (uploadSuccess) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80))
                    Spacer(Modifier.width(8.dp))
                    Text("Tape Rip Uploaded to Cloud Vault!", color = Color(0xFF4ADE80), style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    isUploading = true
                    uploadSuccess = false
                },
                enabled = !isUploading && title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber),
            ) {
                Text(
                    text = if (isUploading) "Calibrating & Uploading..." else "Start Tape Rip & Upload",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1E293B),
                )
            }

            if (isUploading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Amber,
                    trackColor = SurfaceElevated,
                )
            }
        }
    }
}
