package com.isdenmois.readish.transfers.lib

import fi.iki.elonen.NanoHTTPD
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FixedResponseTest {
    @Test
    fun success_isJsonOk() {
        // Act
        val response = FixedResponse.success()
        val body = response.data.readBytes().toString(Charsets.UTF_8)

        // Assert
        assertEquals(NanoHTTPD.Response.Status.OK, response.status)
        assertEquals("application/json", response.mimeType)
        assertEquals("{ \"OK\": true }", body)
    }

    @Test
    fun badRequest_isJsonWithMessage() {
        // Act
        val response = FixedResponse.badRequest("no file")
        val body = response.data.readBytes().toString(Charsets.UTF_8)

        // Assert
        assertEquals(NanoHTTPD.Response.Status.BAD_REQUEST, response.status)
        assertEquals("application/json", response.mimeType)
        assertTrue(body.contains("no file"))
    }
}
