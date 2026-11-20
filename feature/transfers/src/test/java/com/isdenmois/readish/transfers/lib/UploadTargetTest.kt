package com.isdenmois.readish.transfers.lib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UploadTargetTest {
    private val uploadsDir = File("/storage/emulated/0/WifiTransfer")

    @Test
    fun plainFileName_landsInUploadsDir() {
        // Act
        val target = uploadTarget(uploadsDir, "dune.fb2")

        // Assert
        assertEquals(File(uploadsDir, "dune.fb2"), target)
    }

    @Test
    fun parentTraversal_staysInsideUploadsDir() {
        // Act
        val target = uploadTarget(uploadsDir, "../escape.fb2")

        // Assert
        assertEquals(File(uploadsDir, "escape.fb2"), target)
        assertTrue(target.canonicalPath.startsWith(uploadsDir.canonicalPath))
    }

    @Test
    fun nestedTraversal_staysInsideUploadsDir() {
        // Act
        val target = uploadTarget(uploadsDir, "../../other/escape.fb2")

        // Assert
        assertTrue(target.canonicalPath.startsWith(uploadsDir.canonicalPath))
    }

    @Test
    fun windowsSeparator_staysInsideUploadsDir() {
        // Act
        val target = uploadTarget(uploadsDir, "..\\escape.fb2")

        // Assert
        assertTrue(target.canonicalPath.startsWith(uploadsDir.canonicalPath))
    }

    @Test
    fun dotOnlyName_isRejected() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) { uploadTarget(uploadsDir, "..") }
    }

    @Test
    fun blankName_isRejected() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) { uploadTarget(uploadsDir, "") }
    }
}
