package com.isdenmois.readish.transfers.lib

import java.io.File

internal fun uploadTarget(uploadsDir: File, clientFileName: String): File {
    val safeName = clientFileName.substringAfterLast('/').substringAfterLast('\\')

    require(safeName.isNotBlank() && safeName != "." && safeName != "..") {
        "Invalid file name: $clientFileName"
    }

    return File(uploadsDir, safeName)
}
