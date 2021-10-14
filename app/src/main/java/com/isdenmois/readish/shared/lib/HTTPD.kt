package com.isdenmois.readish.shared.lib

import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response
import fi.iki.elonen.NanoHTTPD.Response.Status
import fi.iki.elonen.router.RouterNanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource
import java.io.File

class HTTPD(private val root: String): RouterNanoHTTPD(port) {
    companion object {
        const val port = 8083

        fun success() = newFixedLengthResponse(Status.OK, "application/json", "{ \"OK\": true }")
        fun badRequest(error: String) = newFixedLengthResponse(Status.BAD_REQUEST, "application/json", "{ \"message\": \"$error\" }")
    }

    init {
        addMappings()
    }

    override fun addMappings() {
        super.addMappings()

        addRoute("/", HomeHandler::class.java)
        addRoute("/upload", UploadHandler::class.java, root)
    }
}

class HomeHandler : DefaultHandler() {
    override fun getMimeType() = "text/plain"

    override fun getStatus() = Status.OK

    override fun getText() = "Everything works OK"
}

class UploadHandler : DefaultHandler() {
    override fun getMimeType() = "application/json"

    override fun getStatus() = Status.OK

    override fun getText() = "{ \"OK\": true }"


    override fun post(uriResource: UriResource, urlParams: Map<String?, String?>?, session: IHTTPSession): Response? {
        val uploadsDir = uriResource.initParameter(String::class.java)
        val files: Map<String, String> = HashMap()

        return try {
            session.parseBody(files)

            files.forEach { (key, tmpPath) ->
                val fileName = session.parameters[key]!![0]
                val output = File(uploadsDir, fileName)
                val tmp = File(tmpPath)

                tmp.inputStream().copyTo(output.outputStream())
                tmp.delete()
            }

            HTTPD.success()
        } catch (e: Exception) {
            e.printStackTrace()
            HTTPD.badRequest(e.message ?: "Something went wrong")
        }
    }
}
