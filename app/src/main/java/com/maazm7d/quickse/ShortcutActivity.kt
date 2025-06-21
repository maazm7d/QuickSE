package com.maazm7d.quickse

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.maazm7d.quickse.util.getSelinuxStatus
import com.maazm7d.quickse.util.setSelinuxMode

class ShortcutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check current mode and toggle
        val currentMode = getSelinuxStatus()
        val newMode = if (currentMode == "Enforcing") "Permissive" else "Enforcing"
        val success = setSelinuxMode(newMode)

        Toast.makeText(this,
            if (success) "Switched to $newMode"
            else "Failed to switch SELinux mode",
            Toast.LENGTH_SHORT).show()

        finish() // Close activity immediately
    }
}

