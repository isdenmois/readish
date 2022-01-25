package com.isdenmois.readish.transfers.lib.handlers

import android.os.Handler
import android.os.Looper
import com.isdenmois.readish.transfers.lib.FileUploadedListener
import com.isdenmois.readish.transfers.lib.FixedResponse
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD
import java.io.File

class UploadHandler : RouterNanoHTTPD.DefaultHandler() {
    override fun getMimeType() = "application/json"

    override fun getStatus() = NanoHTTPD.Response.Status.OK

    override fun getText() = "{ \"OK\": true }"

    override fun post(
        uriResource: RouterNanoHTTPD.UriResource,
        urlParams: Map<String?, String?>?,
        session: NanoHTTPD.IHTTPSession
    ): NanoHTTPD.Response? {
        val uploadsDir = uriResource.initParameter(String::class.java)
        val fileUploadedListener = uriResource.initParameter(1, FileUploadedListener::class.java)
        val files: Map<String, String> = HashMap()

        return try {
            session.parseBody(files)

            files.forEach { (key, tmpPath) ->
                val fileName = session.parameters[key]!![0]
                val output = File(uploadsDir, fileName)
                val tmp = File(tmpPath)

                tmp.inputStream().copyTo(output.outputStream())
                tmp.delete()

                Handler(Looper.getMainLooper()).post {
                    fileUploadedListener?.onFileUploaded(output)
                }
            }

            FixedResponse.success()
        } catch (e: Exception) {
            e.printStackTrace()
            FixedResponse.badRequest(e.message ?: "Something went wrong")
        }
    }
}
