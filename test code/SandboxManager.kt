package com.example.ghostbrowse

import android.content.Context
import java.io.File
import java.util.UUID

class SandboxManager(private val context: Context) {
    private lateinit var sessionDir: File

    // Create a unique temp directory for this session
    fun createSession(): File {
        val sessionId = UUID.randomUUID().toString()
        sessionDir = File(context.cacheDir, "disposable_session_$sessionId").apply {
            mkdirs() // Create the directory if it doesn’t exist
        }
        return sessionDir
    }

    // Delete the session directory and its contents
    fun destroySession() {
        sessionDir.deleteRecursively()
    }
}