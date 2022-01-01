package com.isdenmois.readish.home.model

import android.webkit.MimeTypeMap

object MimeUtils {
    private val mimeMap = hashMapOf(
        "fb2" to "application/x-fictionbook",
        "epub" to "application/epub",
        "zip" to "application/x-fictionbook+zip",
    )

    fun getByFileName(fileName: String): String {
        val lastdot = fileName.lastIndexOf(".")

        if (lastdot > 0) {
            val ext = fileName.substring(lastdot + 1).lowercase()

            if (mimeMap.containsKey(ext)) {
                return mimeMap[ext]!!
            }

            val type = MimeTypeMap.getSingleton().getExtensionFromMimeType(ext)

            if (type != null) {
                return type
            }

            if (ext == "fb2") return "application/x-fictionbook"
        }

        return "*/*"
    }
}
