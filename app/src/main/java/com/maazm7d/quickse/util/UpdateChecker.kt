package com.maazm7d.quickse.util

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.maazm7d.quickse.BuildConfig
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object UpdateChecker {

    private const val GITHUB_RELEASES_URL = "https://api.github.com/repos/maazm7d/QuickSE/releases/latest"

    fun checkForUpdate(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(GITHUB_RELEASES_URL).build()
                val response = client.newCall(request).execute()

                val body = response.body?.string() ?: return@launch
                val json = JSONObject(body)
                val latestTag = json.getString("tag_name").removePrefix("v")
                val currentVersion = BuildConfig.VERSION_NAME

                val assets = json.getJSONArray("assets")
                val apkUrl = (0 until assets.length())
                    .map { assets.getJSONObject(it) }
                    .firstOrNull { it.getString("name").endsWith(".apk") }
                    ?.getString("browser_download_url")

                if (apkUrl != null && isNewerVersion(latestTag, currentVersion)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Update available: v$latestTag", Toast.LENGTH_SHORT).show()
                        downloadAndInstallApk(context, apkUrl)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "You're on the latest version.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Update check failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    private fun downloadAndInstallApk(context: Context, apkUrl: String) {
        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("QuickSE Update")
            setDescription("Downloading latest version...")
            setMimeType("application/vnd.android.package-archive")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "QuickSE-latest.apk")
        }

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    val apkUri = manager.getUriForDownloadedFile(downloadId)
                    val install = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(apkUri, "application/vnd.android.package-archive")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
}

