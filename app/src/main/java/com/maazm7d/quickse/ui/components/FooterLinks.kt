package com.maazm7d.quickse.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import com.maazm7d.quickse.R

@Composable
fun FooterLinks(
    onGitHubClick: () -> Unit,
    onTelegramClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButtonWithText(
                painterRes = R.drawable.ic_github,
                text = "GitHub",
                onClick = onGitHubClick
            )
            Spacer(modifier = Modifier.width(24.dp))
            IconButtonWithText(
                painterRes = R.drawable.ic_telegram,
                text = "Telegram",
                onClick = onTelegramClick
            )
        }
    }
}

@Composable
fun IconButtonWithText(
    painterRes: Int,
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = painterRes),
            contentDescription = text,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

