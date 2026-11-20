package com.isdenmois.readish.transfers.lib

import org.junit.Assert.assertEquals
import org.junit.Test

class IpAddressTest {
    @Test
    fun formatsLittleEndianAddressAsDottedQuad() {
        // Arrange
        val ipAddress = 0x0501A8C0 // 192.168.1.5, little-endian

        // Act
        val formatted = formatIpAddress(ipAddress)

        // Assert
        assertEquals("192.168.1.5", formatted)
    }

    @Test
    fun formatsZeroAddress() {
        // Act
        val formatted = formatIpAddress(0)

        // Assert
        assertEquals("0.0.0.0", formatted)
    }
}
