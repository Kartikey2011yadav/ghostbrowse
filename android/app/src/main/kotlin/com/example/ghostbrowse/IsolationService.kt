package com.example.ghostbrowse

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.io.File

class IsolationService : Service() {
    private lateinit var tempDir: File
    private lateinit var testDir: File

    companion object {
        var tempDirPath: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        // Create a temp directory in the app's cache directory
        testDir = File(cacheDir, "test")
        testDir.mkdirs()
        // Create a unique temp directory for this instance
        tempDir = File(cacheDir, "isolation_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        tempDirPath = tempDir.absolutePath
        android.util.Log.d("IsolationService", "Temp dir created: ${tempDir.absolutePath}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("URL") ?: return START_NOT_STICKY
        // Log or perform background tasks (e.g., monitor URL safety) if needed
        android.util.Log.d("IsolationService", "Started for URL: $url, Temp dir: ${tempDir.absolutePath}")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        tempDir.deleteRecursively() // Clean up temp files
        tempDirPath = null
        android.util.Log.d("IsolationService", "Instance destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

}