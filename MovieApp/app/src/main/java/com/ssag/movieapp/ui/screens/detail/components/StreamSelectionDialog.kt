package com.ssag.movieapp.ui.screens.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssag.movieapp.data.model.StreamLinkDto
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface

@Composable
fun StreamSelectionDialog(
    streams: List<StreamLinkDto>,
    qualities: Map<Int, String>,
    onDismiss: () -> Unit,
    onStreamSelected: (StreamLinkDto) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FlixSurface,
        title = {
            Text(
                text = "Select Quality",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(streams) { stream ->
                    val qualityName = qualities[stream.quality] ?: "Unknown"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onStreamSelected(stream)
                                onDismiss()
                            },
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = null,
                                tint = FlixPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = qualityName,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (stream.size_mb > 0) {
                                    Text(
                                        text = "${stream.size_mb} MB",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FlixPrimary)
            }
        }
    )
}
