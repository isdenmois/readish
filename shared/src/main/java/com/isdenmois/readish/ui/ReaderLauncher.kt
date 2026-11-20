package com.isdenmois.readish.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.File

class ReaderLauncher(private val context: Context) {
    fun openReader() {
        try {
            context.startActivity(context.packageManager.getLaunchIntentForPackage(READER_PACKAGE))
        } catch (e: Exception) {
            e.printStackTrace()
            showCantStart()
        }
    }

    fun openBook(file: File) {
        val intent =
            context.packageManager.getLaunchIntentForPackage(READER_PACKAGE)?.apply {
                setDataAndType(Uri.fromFile(file), MimeUtils.getByFileName(file.name))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showCantStart()
        }
    }

    fun openOnyxHome() {
        val intent =
            Intent().apply {
                component = ComponentName("com.onyx", "com.onyx.reader.main.ui.MainActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showCantStart()
        }
    }

    private fun showCantStart() {
        Toast.makeText(context, context.getString(R.string.cant_start_application), Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val READER_PACKAGE = "com.neverland.alreaderprofs"
    }
}
