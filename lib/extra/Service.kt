package com.example.ghostbrowse

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.webkit.WebView
import java.io.File

class DisposableBrowserService : Service() {
    private lateinit var webView: WebView
    private lateinit var tempDir: File

    override fun onCreate() {
        super.onCreate()
        // Create a unique temp directory for this instance
        tempDir = File(cacheDir, "disposable_${System.currentTimeMillis()}")
        tempDir.mkdirs()

        // Initialize WebView with isolated settings
        webView = WebView(this).apply {
            settings.javaScriptEnabled = false // Disable JS for safety
//            settings.setAppCachePath(tempDir.absolutePath)
//            settings.setAppCacheEnabled(true)
            settings.setGeolocationEnabled(false)
            settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("URL") ?: return START_NOT_STICKY
        webView.loadUrl(url)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
        tempDir.deleteRecursively() // Clean up temp files
    }

    override fun onBind(intent: Intent?): IBinder? = null
}