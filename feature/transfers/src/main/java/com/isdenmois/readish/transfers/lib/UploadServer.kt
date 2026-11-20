package com.isdenmois.readish.transfers.lib

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UploadServer(
    private val uploadsDir: File,
    private val onFileUploaded: (File) -> Unit,
    private val port: Int = PORT,
    private val callbackDispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    fun start() {
        server =
            embeddedServer(CIO, port = port) {
                routing {
                    get("/") {
                        call.respondText("Everything works OK", ContentType.Text.Plain, HttpStatusCode.OK)
                    }

                    post("/upload") {
                        try {
                            call.receiveMultipart().forEachPart { part ->
                                if (part is PartData.FileItem) {
                                    val name = part.originalFileName ?: error("Missing file name")
                                    val file =
                                        part.provider().toInputStream().use { input ->
                                            saveUpload(uploadsDir, name, input)
                                        }

                                    withContext(callbackDispatcher) { onFileUploaded(file) }
                                }

                                part.dispose()
                            }

                            call.respondText("""{ "OK": true }""", ContentType.Application.Json, HttpStatusCode.OK)
                        } catch (e: Exception) {
                            call.respondText(errorJson(e), ContentType.Application.Json, HttpStatusCode.BadRequest)
                        }
                    }
                }
            }.start(wait = false)
    }

    fun stop() {
        server?.stop(gracePeriodMillis = 0, timeoutMillis = 1_000)
        server = null
    }

    companion object {
        const val PORT = 8083

        private fun errorJson(error: Throwable): String {
            val message =
                (error.message ?: "Something went wrong")
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")

            return """{ "message": "$message" }"""
        }
    }
}
