package com.isdenmois.readish.transfers.lib.handlers

import android.os.Looper
import com.isdenmois.readish.transfers.lib.FileUploadedListener
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class UploadHandlerTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun post_copiesFileIntoUploadsDirAndNotifiesListener() {
        // Arrange
        val uploadsDir = tempFolder.newFolder("uploads")
        val body = tempFolder.newFile("nano.tmp").apply { writeText("book body") }
        val uploaded = mutableListOf<File>()
        val listener =
            object : FileUploadedListener {
                override fun onFileUploaded(file: File) {
                    uploaded.add(file)
                }
            }
        val session =
            FakeSession(
                parameters = mapOf("file" to listOf("dune.fb2")),
                body = mapOf("file" to body.absolutePath),
            )

        // Act
        val response = UploadHandler().post(uriResource(uploadsDir, listener), emptyMap(), session)
        shadowOf(Looper.getMainLooper()).idle()

        // Assert
        assertEquals(NanoHTTPD.Response.Status.OK, response?.status)
        assertEquals("book body", File(uploadsDir, "dune.fb2").readText())
        assertEquals(listOf(File(uploadsDir, "dune.fb2")), uploaded)
        assertFalse(body.exists())
    }

    @Test
    fun post_keepsTraversalAttemptsInsideUploadsDir() {
        // Arrange
        val uploadsDir = tempFolder.newFolder("uploads")
        val body = tempFolder.newFile("nano.tmp").apply { writeText("escape") }
        val session =
            FakeSession(
                parameters = mapOf("file" to listOf("../escape.fb2")),
                body = mapOf("file" to body.absolutePath),
            )

        // Act
        val response = UploadHandler().post(uriResource(uploadsDir), emptyMap(), session)

        // Assert
        assertEquals(NanoHTTPD.Response.Status.OK, response?.status)
        assertEquals("escape", File(uploadsDir, "escape.fb2").readText())
        assertFalse(File(tempFolder.root, "escape.fb2").exists())
    }

    @Test
    fun post_returnsBadRequestWhenBodyCannotBeParsed() {
        // Arrange
        val uploadsDir = tempFolder.newFolder("uploads")
        val session =
            FakeSession(
                parameters = emptyMap(),
                body = emptyMap(),
                failure = IOException("malformed multipart"),
            )

        // Act
        val response = UploadHandler().post(uriResource(uploadsDir), emptyMap(), session)

        // Assert
        val responseBody = response!!.data.readBytes().toString(Charsets.UTF_8)
        assertEquals(NanoHTTPD.Response.Status.BAD_REQUEST, response.status)
        assertTrue(responseBody.contains("malformed multipart"))
        assertEquals(0, uploadsDir.listFiles()!!.size)
    }

    private fun uriResource(uploadsDir: File, listener: FileUploadedListener? = null) =
        RouterNanoHTTPD.UriResource("/upload", UploadHandler::class.java, uploadsDir.absolutePath, listener)

    private class FakeSession(
        private val parameters: Map<String, List<String>>,
        private val body: Map<String, String>,
        private val failure: IOException? = null,
    ) : NanoHTTPD.IHTTPSession {
        override fun execute() = Unit

        override fun getCookies(): NanoHTTPD.CookieHandler? = null

        override fun getHeaders(): Map<String, String> = emptyMap()

        override fun getInputStream(): java.io.InputStream = java.io.ByteArrayInputStream(ByteArray(0))

        override fun getMethod() = NanoHTTPD.Method.POST

        override fun getParms(): Map<String, String> = emptyMap()

        override fun getParameters(): Map<String, List<String>> = parameters

        override fun getQueryParameterString() = ""

        override fun getUri() = "/upload"

        override fun parseBody(files: MutableMap<String, String>) {
            failure?.let { throw it }

            files.putAll(body)
        }

        override fun getRemoteIpAddress() = "127.0.0.1"

        override fun getRemoteHostName() = "localhost"
    }
}
