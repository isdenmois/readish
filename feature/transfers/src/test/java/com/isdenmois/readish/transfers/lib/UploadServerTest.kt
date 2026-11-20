package com.isdenmois.readish.transfers.lib

import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.net.HttpURLConnection
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.util.concurrent.CopyOnWriteArrayList

class UploadServerTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var uploadsDir: File
    private lateinit var server: UploadServer
    private val uploaded = CopyOnWriteArrayList<File>()
    private var port = 0

    @Before
    fun setUp() {
        uploadsDir = tempFolder.newFolder("uploads")
        port = ServerSocket(0).use { it.localPort }
        server =
            UploadServer(
                uploadsDir = uploadsDir,
                onFileUploaded = { uploaded.add(it) },
                port = port,
                callbackDispatcher = Dispatchers.Unconfined,
            )
        server.start()
        waitUntilUp()
    }

    @After
    fun tearDown() {
        server.stop()
    }

    @Test
    fun healthCheck_returnsPlainText() {
        // Act
        val response = get("/")

        // Assert
        assertEquals(200, response.first)
        assertEquals("Everything works OK", response.second)
    }

    @Test
    fun upload_savesFileAndNotifiesListener() {
        // Act
        val status = upload("dune.fb2", "book body")

        // Assert
        assertEquals(200, status)
        assertEquals("book body", File(uploadsDir, "dune.fb2").readText())
        assertEquals(listOf(File(uploadsDir, "dune.fb2")), uploaded)
    }

    @Test
    fun upload_keepsTraversalAttemptsInsideUploadsDir() {
        // Act
        val status = upload("../escape.fb2", "escape")

        // Assert
        assertEquals(200, status)
        assertEquals("escape", File(uploadsDir, "escape.fb2").readText())
        assertFalse(File(tempFolder.root, "escape.fb2").exists())
    }

    @Test
    fun upload_returnsBadRequestForNonMultipartBody() {
        // Arrange
        val connection = URL("http://127.0.0.1:$port/upload").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "text/plain")

        // Act
        connection.outputStream.use { it.write("not multipart".toByteArray()) }
        val status = connection.responseCode

        // Assert
        assertEquals(400, status)
    }

    private fun waitUntilUp() {
        repeat(50) {
            try {
                Socket("127.0.0.1", port).use { return }
            } catch (_: Exception) {
                Thread.sleep(20)
            }
        }
        error("UploadServer did not start on port $port")
    }

    private fun get(path: String): Pair<Int, String> {
        val connection = URL("http://127.0.0.1:$port$path").openConnection() as HttpURLConnection

        return connection.responseCode to connection.inputStream.bufferedReader().readText()
    }

    private fun upload(fileName: String, body: String): Int {
        val boundary = "----readish-test"
        val payload =
            buildString {
                append("--$boundary\r\n")
                append("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"\r\n")
                append("Content-Type: application/octet-stream\r\n\r\n")
                append(body)
                append("\r\n--$boundary--\r\n")
            }
        val connection = URL("http://127.0.0.1:$port/upload").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        connection.outputStream.use { it.write(payload.toByteArray()) }

        return connection.responseCode
    }
}
