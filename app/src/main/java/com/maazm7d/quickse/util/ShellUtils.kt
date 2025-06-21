package com.maazm7d.quickse.util

import java.io.BufferedReader
import java.io.InputStreamReader

fun runCommand(command: String): String {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.readLine().trim()
    } catch (e: Exception) {
        ""
    }
}

fun getSelinuxStatus(): String {
    val result = runCommand("su -c getenforce")
    return if (result.equals("Enforcing", true)) "Enforcing" else "Permissive"
}

fun setSelinuxMode(mode: String): Boolean {
    runCommand("su -c setenforce ${if (mode == "Enforcing") 1 else 0}")
    return getSelinuxStatus() == mode
}

