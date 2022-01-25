package com.isdenmois.readish.transfers.lib

import java.io.File

interface FileUploadedListener {
    fun onFileUploaded(file: File)
}
