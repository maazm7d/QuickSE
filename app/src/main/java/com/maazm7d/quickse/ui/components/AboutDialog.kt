package com.maazm7d.quickse.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutDialog(versionName: String, onDismiss: () -> Unit, context: android.content.Context)  {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "About QuickSE",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Version: $versionName")
                Spacer(Modifier.height(8.dp))
                Text("Developer: maazm7d")
                Spacer(Modifier.height(16.dp))
                Text(
                    "This app shows current SELinux status and allows switching between modes.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
              androidx.compose.material3.TextButton(
                onClick = {
                    com.maazm7d.quickse.util.UpdateChecker.checkForUpdate(context)
                }
            ) {
                Text("Check for Updates")
            }
        }
    )
}

