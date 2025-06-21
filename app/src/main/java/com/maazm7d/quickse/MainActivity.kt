package com.maazm7d.quickse

import com.maazm7d.quickse.util.getSelinuxStatus
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.maazm7d.quickse.ui.screens.MainScreen
import com.maazm7d.quickse.ui.theme.QuickSETheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
lifecycleScope.launch {
    delay(1000) // Delay to ensure root access is ready
    val currentStatus = getSelinuxStatus()
    if (currentStatus == "Unknown") return@launch // Skip if root not granted

    val nextStatus = if (currentStatus == "Enforcing") "Permissive" else "Enforcing"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        val shortcutManager = getSystemService(ShortcutManager::class.java)

        val shortcutIntent = Intent(this@MainActivity, ShortcutActivity::class.java).apply {
            action = Intent.ACTION_VIEW
        }

        val shortcut = ShortcutInfo.Builder(this@MainActivity, "toggle_selinux")
            .setShortLabel("Switch to $nextStatus")
            .setLongLabel("Switch SELinux to $nextStatus")
            .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.ic_launcher))
            .setIntent(shortcutIntent)
            .build()

        shortcutManager.dynamicShortcuts = listOf(shortcut)
    }
}


        setContent {
            QuickSETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}
