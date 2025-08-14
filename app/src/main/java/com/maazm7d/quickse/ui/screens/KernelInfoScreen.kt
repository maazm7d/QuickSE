package com.maazm7d.quickse.ui.screens

import android.content.*
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import java.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KernelInfoScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val kernelEntries = listOf(
        "Kernel Information" to readShell("uname -a"),
        "Kernel Version" to readFile("/proc/version"),
        "Loaded Kernel Modules" to readModules(),
        "Kernel Symbols" to "Tap to load kernel symbols...",
        "Kernel Configuration" to readShell("zcat /proc/config.gz"),
        "System Uptime" to systemUptime(readFile("/proc/uptime")),
        "System Load Average" to systemLoadAvg(readFile("/proc/loadavg")),
        "CPU Information" to readFile("/proc/cpuinfo"),
        "Memory Information" to readMemInfoMB(),
        "SELinux Status" to readShell("getenforce"),
        "Kernel Logs (Tail)" to readShell("dmesg | tail -n 100")
    )


    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) kernelEntries
        else kernelEntries.filter {
            it.first.contains(searchQuery, true) || it.second.contains(searchQuery, true)
        }
    }

    CompositionLocalProvider(LocalSearchQuery provides searchQuery) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Kernel Inspector", fontFamily = FontFamily.Monospace) }
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        placeholder = { Text("Search Kernel Info...", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        singleLine = true
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                filtered.forEach { (title, content) ->
                    ExpandableKernelCard(title = title, fullText = content, context = context)
                }
            }
        }
    }
}

@Composable
fun ExpandableKernelCard(title: String, fullText: String, context: Context) {
    val isExpandable = title == "Kernel Symbols" || fullText.lineCount() > 5
    var content by remember { mutableStateOf(fullText) }
    var expanded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val search = LocalSearchQuery.current

    LaunchedEffect(expanded) {
        if (expanded && title == "Kernel Symbols" && content.startsWith("Tap to load")) {
            loading = true
            withContext(Dispatchers.IO) {
                try {
                    val lines = File("/proc/kallsyms")
                        .useLines { it.take(200).joinToString("\n") }
                    content = lines + "\n... [Truncated]"
                } catch (e: Exception) {
                    content = "⚠️ Unable to read: ${e.message}"
                }
            }
            loading = false
        }
    }

    val displayText = when {
        loading -> "Loading..."
        expanded -> content
        else -> content.lineSequence().take(5).joinToString("\n") + "\n..."
    }

    val highlightedText = remember(displayText, search) {
        highlightQueryText(displayText, search)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = isExpandable) {
                expanded = !expanded
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Monospace)
                Row {
                    IconButton(onClick = { copyToClipboard(context, title, content) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    IconButton(onClick = { saveToFile(context, title, content) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = highlightedText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


fun systemUptime(raw: String): String {
    return try {
        val uptimeSeconds = raw.split(" ")[0].toDoubleOrNull() ?: return raw
        val hours = (uptimeSeconds / 3600).toInt()
        val minutes = ((uptimeSeconds % 3600) / 60).toInt()
        val seconds = (uptimeSeconds % 60).toInt()

        "🕓 Up for ${hours}h ${minutes}m ${seconds}s"
    } catch (e: Exception) {
        "⚠️ Invalid uptime data"
    }
}

fun systemLoadAvg(raw: String): String {
    val parts = raw.split(" ")
    return if (parts.size >= 5) {
        val one = parts[0]
        val five = parts[1]
        val fifteen = parts[2]
        val running = parts[3]
        val pid = parts[4]

        """
        📊 Load Avg (1/5/15 min): $one / $five / $fifteen
        ⚙️ Running Processes: $running
        🔢 Last PID: $pid
        """.trimIndent()
    } else {
        "⚠️ Invalid loadavg data"
    }
}

fun readModules(): String = try {
    val content = File("/proc/modules").readText().trim()
    if (content.isEmpty()) "No loaded kernel modules found"
    else content
} catch (e: Exception) {
    "⚠️ Cannot read /proc/modules: ${e.message}"
}

fun readMemInfoMB(): String = try {
    File("/proc/meminfo")
        .readLines()
        .joinToString("\n") { line ->
            val parts = line.trim().split(Regex("\\s+"))
            if (parts.size >= 2 && parts[1].toIntOrNull() != null)
                "${parts[0]} ${parts[1]} kB (${String.format("%.1f", parts[1].toFloat() / 1024)} MB)"
            else line
        }
} catch (e: Exception) {
    "⚠️ Couldn't read /proc/meminfo: ${e.message}"
}

fun highlightQueryText(fullText: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(fullText)

    val lcText = fullText.lowercase()
    val lcQuery = query.lowercase()
    val builder = buildAnnotatedString {
        var startIndex = 0
        while (startIndex < lcText.length) {
            val index = lcText.indexOf(lcQuery, startIndex)
            if (index == -1) {
                append(fullText.substring(startIndex))
                break
            }
            append(fullText.substring(startIndex, index))
            withStyle(
                style = SpanStyle(
                    background = Color.Yellow.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(fullText.substring(index, index + query.length))
            }
            startIndex = index + query.length
        }
    }
    return builder
}

val LocalSearchQuery = staticCompositionLocalOf { "" }

fun String.lineCount(): Int = count { it == '\n' } + 1

fun copyToClipboard(context: Context, label: String, text: String) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Copy failed", Toast.LENGTH_SHORT).show()
    }
}

fun saveToFile(context: Context, title: String, content: String) {
    try {
        val filename = title.toSafeFileName()
        val file = File(context.getExternalFilesDir(null), "$filename.txt")
        FileOutputStream(file).use { it.write(content.toByteArray()) }
        Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Saving failed", Toast.LENGTH_SHORT).show()
    }
}

fun String.toSafeFileName(): String =
    replace("/", "_").replace(" ", "_").replace(Regex("[^a-zA-Z0-9_]"), "")

fun readFile(path: String): String = try {
    File(path).readText().trim()
} catch (_: Exception) {
    "⚠️ Permission denied or unavailable: $path"
}

fun readShell(cmd: String): String = try {
    val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
    val output = process.inputStream.bufferedReader().readText().trim()
    output.ifEmpty { "No output" }
} catch (_: Exception) {
    "⚠️ Shell command failed: $cmd"
}
