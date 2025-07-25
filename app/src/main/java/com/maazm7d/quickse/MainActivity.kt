package com.maazm7d.quickse

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.maazm7d.quickse.ui.navigation.AppNavGraph
import com.maazm7d.quickse.ui.screens.RootAccessRequiredScreen
import com.maazm7d.quickse.ui.theme.QuickSETheme
import com.maazm7d.quickse.util.getSelinuxStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController

val hasRoot = mutableStateOf(true)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val currentStatus = getSelinuxStatus()
            if (currentStatus == "Unknown") {
                hasRoot.value = false
                return@launch
            }

            val shortcutManager = getSystemService(ShortcutManager::class.java)

            val shortcutIntent = Intent(this@MainActivity, ShortcutActivity::class.java).apply {
                action = Intent.ACTION_VIEW
            }

            val shortcut = ShortcutInfo.Builder(this@MainActivity, "toggle_selinux")
                .setShortLabel(getString(R.string.shortcut_short_label))
                .setLongLabel(getString(R.string.shortcut_long_label))
                .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.ic_launcher))
                .setIntent(shortcutIntent)
                .build()

            shortcutManager.dynamicShortcuts = listOf(shortcut)
        }

        setContent {
            QuickSETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasRoot.value) {
                        val navController = rememberNavController()
                        AppNavGraph(navController = navController)
                    } else {
                        RootAccessRequiredScreen(
                            onRetry = {
                                val status = getSelinuxStatus()
                                val success = status != "Unknown"
                                hasRoot.value = success
                                if (!success) {
                                    Toast.makeText(this@MainActivity, getString(R.string.root_access_not_available), Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

    
