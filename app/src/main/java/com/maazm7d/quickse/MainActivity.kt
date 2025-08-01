package com.maazm7d.quickse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.maazm7d.quickse.ui.theme.QuickSETheme
import com.maazm7d.quickse.util.getSelinuxStatus
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import com.maazm7d.quickse.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
    val currentStatus = getSelinuxStatus()
    if (currentStatus == "Unknown") return@launch

    val shortcutManager = getSystemService(ShortcutManager::class.java)

    val shortcutIntent = Intent(this@MainActivity, ShortcutActivity::class.java).apply {
        action = Intent.ACTION_VIEW
    }

    val shortcut = ShortcutInfo.Builder(this@MainActivity, "toggle_selinux")
        .setShortLabel("Toggle state")
        .setLongLabel("Toggle SELinux state")
        .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.ic_launcher))
        .setIntent(shortcutIntent)
        .build()

    shortcutManager.dynamicShortcuts = listOf(shortcut)
}


        setContent {
    QuickSETheme {
        val navController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavGraph(navController = navController)
           
                }
            }
        }
    } 
} 
    

    
