package com.example.ghostbrowse

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.ghostbrowse/disposable"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startDisposableInstance" -> {
                    val url = call.argument<String>("url")
                    val tempDirPath = startDisposableInstance(url)
//                    result.success("Instance started")
                    result.success(mapOf("message" to "Instance started", "tempDir" to tempDirPath))
                }
                "stopDisposableInstance" -> {
                    stopDisposableInstance()
                    result.success("Instance stopped")
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startDisposableInstance(url: String?): String {
        val intent = Intent(this, IsolationService::class.java).apply {
            putExtra("URL", url)
        }
        startService(intent)
        // Return the tempDir from IsolationService
        return IsolationService.tempDirPath ?: "/data/data/com.example.ghostbrowse/cache/default"
    }

    private fun stopDisposableInstance() {
        val intent = Intent(this, IsolationService::class.java)
        stopService(intent)
    }
}