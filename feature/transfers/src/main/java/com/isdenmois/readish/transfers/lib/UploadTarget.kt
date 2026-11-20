package com.isdenmois.readish.transfers.lib

import java.io.File
import java.io.InputStream

internal fun saveUpload(uploadsDir: File, clientFileName: String, input: InputStream): File {
    val target = uploadTarget(uploadsDir, clientFileName)

    input.use { source ->
        target.outputStream().use { destination -> source.copyTo(destination) }
    }

    return target
}

internal fun uploadTarget(uploadsDir: File, clientFileName: String): File {
    val safeName = clientFileName.substringAfterLast('/').substringAfterLast('\\')

    require(safeName.isNotBlank() && safeName != "." && safeName != "..") {
        "Invalid file name: $clientFileName"
    }

    return File(uploadsDir, safeName)
}
