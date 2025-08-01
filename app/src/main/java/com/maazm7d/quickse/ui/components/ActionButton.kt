package com.maazm7d.quickse.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    currentStatus: String?,
    isRootAvailable: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isRootAvailable && currentStatus != null,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = when (currentStatus?.lowercase()) {
                "enforcing" -> "Switch to Permissive"
                "permissive" -> "Switch to Enforcing"
                else -> "Retry"
            },
            style = MaterialTheme.typography.titleMedium
        )
    }
}
