package com.isdenmois.readish.transfers.lib.handlers

import fi.iki.elonen.NanoHTTPD
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeHandlerTest {
    @Test
    fun servesPlainTextHealthCheck() {
        // Act
        val handler = HomeHandler()

        // Assert
        assertEquals("text/plain", handler.mimeType)
        assertEquals(NanoHTTPD.Response.Status.OK, handler.status)
        assertEquals("Everything works OK", handler.text)
    }
}
