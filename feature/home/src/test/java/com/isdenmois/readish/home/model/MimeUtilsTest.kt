package com.isdenmois.readish.home.model

import android.webkit.MimeTypeMap
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class MimeUtilsTest {
    @Test
    fun bundledExtension_mapsFromBundledTable() {
        // Arrange
        val fileName = "book.fb2"

        // Act
        val mime = MimeUtils.getByFileName(fileName)

        // Assert
        assertEquals("application/x-fictionbook", mime)
    }

    @Test
    fun bundledExtension_isCaseInsensitive() {
        // Act
        val mime = MimeUtils.getByFileName("BOOK.EPUB")

        // Assert
        assertEquals("application/epub", mime)
    }

    @Test
    fun knownSystemExtension_mapsFromSystemMimeTypeMap() {
        // Arrange
        shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypeMapping("pdf", "application/pdf")

        // Act
        val mime = MimeUtils.getByFileName("book.pdf")

        // Assert
        assertEquals("application/pdf", mime)
    }

    @Test
    fun unknownExtension_returnsWildcard() {
        // Act
        val mime = MimeUtils.getByFileName("book.unknownext")

        // Assert
        assertEquals("*/*", mime)
    }

    @Test
    fun nameWithoutDot_returnsWildcard() {
        // Act
        val mime = MimeUtils.getByFileName("book")

        // Assert
        assertEquals("*/*", mime)
    }

    @Test
    fun nameStartingWithDot_returnsWildcard() {
        // Act
        val mime = MimeUtils.getByFileName(".fb2")

        // Assert
        assertEquals("*/*", mime)
    }
}
