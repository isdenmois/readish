package com.isdenmois.readish.ui

import android.webkit.MimeTypeMap

object MimeUtils {
    private val mimeMap =
        hashMapOf(
            "fb2" to "application/x-fictionbook",
            "epub" to "application/epub",
            "zip" to "application/x-fictionbook+zip",
        )

    fun getByFileName(fileName: String): String {
        val lastdot = fileName.lastIndexOf(".")

        if (lastdot > 0) {
            val ext = fileName.substring(lastdot + 1).lowercase()

            mimeMap[ext]?.let { return it }

            val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)

            if (type != null) {
                return type
            }
        }

        return "*/*"
    }
}
