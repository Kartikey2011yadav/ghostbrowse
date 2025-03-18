package com.example.ghostbrowse

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example/isolation"
    private lateinit var sandboxManager: SandboxManager

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        sandboxManager = SandboxManager(this)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startWebSession" -> {
                    val url = call.argument<String>("url") ?: ""
                    val sessionDir = sandboxManager.createSession()
                    val webView = IsolatedWebView(this, sessionDir)
                    webView.loadIsolatedUrl(url)
                    result.success("Web session started")
                }
                "startPdfSession" -> {
                    val pdfPath = call.argument<String>("pdfPath") ?: ""
                    val sessionDir = sandboxManager.createSession()
                    val pdfFile = File(pdfPath).copyTo(File(sessionDir, "temp.pdf"), overwrite = true)
                    val pdfRenderer = IsolatedPdfRenderer(this)
                    // Logic to render PDF to an ImageView and pass back to Flutter (simplified here)
                    result.success("PDF session started")
                }
                "destroySession" -> {
                    sandboxManager.destroySession()
                    result.success("Session destroyed")
                }
                else -> result.notImplemented()
            }
        }
    }
}
