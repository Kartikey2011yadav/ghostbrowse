package com.example.ghostbrowse

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class DisposableBrowserActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var tempDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a unique temp directory for this instance
        tempDir = File(cacheDir, "disposable_${System.currentTimeMillis()}")
        tempDir.mkdirs()

        // Initialize WebView
        webView = WebView(this).apply {
            settings.javaScriptEnabled = true // Enable JS for full browser functionality
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true // Enable DOM storage for modern sites
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true // Support responsive layouts

            // Clear cache for isolation
            clearCache(true)

            // Custom WebViewClient for basic navigation control
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url ?: return false)
                    return true
                }
            }
        }

        // Set WebView as the content view
        setContentView(webView)

        // Load URL from intent
        val url = intent.getStringExtra("URL") ?: "https://google.com"
        webView.loadUrl(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up WebView and temp files
        webView.clearCache(true)
        webView.destroy()
        tempDir.deleteRecursively()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack() // Support back navigation like a browser
        } else {
            super.onBackPressed() // Close the activity if no back history
        }
    }
}