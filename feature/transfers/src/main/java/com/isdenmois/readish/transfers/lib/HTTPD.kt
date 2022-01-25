package com.isdenmois.readish.transfers.lib

import com.isdenmois.readish.transfers.lib.handlers.HomeHandler
import com.isdenmois.readish.transfers.lib.handlers.UploadHandler
import fi.iki.elonen.router.RouterNanoHTTPD

class HTTPD(
    private val root: String,
    private var fileUploadedListener: FileUploadedListener? = null,
) : RouterNanoHTTPD(port) {
    companion object {
        const val port = 8083
    }

    init {
        addMappings()
    }

    override fun addMappings() {
        super.addMappings()

        addRoute("/", HomeHandler::class.java)
        addRoute("/upload", UploadHandler::class.java, root, fileUploadedListener)
    }
}
