package com.example.ghostbrowse

import android.webkit.WebView
import android.webkit.WebSettings
import android.webkit.WebViewClient

class IsolatedWebView(context: Context, sessionDir: File) : WebView(context) {
    init {
        // Configure WebView settings for isolation
        settings.apply {
            javaScriptEnabled = false // Disable JS by default (enable only if needed)
            allowFileAccess = false // No local file access
            allowContentAccess = false
            setCacheMode(WebSettings.LOAD_NO_CACHE) // Avoid persistent caching
            databaseEnabled = false
            domStorageEnabled = false
        }

        // Set a custom cache directory for this session
        setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Restrict navigation to the original URL (optional)
                return false
            }
        })

        // Use the session-specific directory for temporary data
        setDataDirectorySuffix(sessionDir.absolutePath)
    }

    // Load a URL in the isolated WebView
    fun loadIsolatedUrl(url: String) {
        loadUrl(url)
    }

    // Cleanup WebView data
    fun destroySession() {
        clearCache(true)
        clearHistory()
        destroy()
    }
}