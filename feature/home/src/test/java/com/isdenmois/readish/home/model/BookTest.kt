package com.isdenmois.readish.home.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class BookTest {
    @Test
    fun readingProgress_isPositionOverSize() {
        // Arrange
        val book = bookOf(position = 1024, size = 2048)

        // Act
        val percent = book.percent

        // Assert
        assertEquals(0.5f, percent, 0.0001f)
    }

    @Test
    fun readingProgress_isZeroWhenBookSizeIsUnknown() {
        // Arrange
        val book = bookOf(position = 500, size = 0)

        // Act
        val percent = book.percent

        // Assert
        assertEquals(0f, percent, 0f)
    }

    @Test
    fun readingProgress_neverExceedsOne() {
        // Arrange
        val book = bookOf(position = 4096, size = 2048)

        // Act
        val percent = book.percent

        // Assert
        assertEquals(1f, percent, 0f)
    }

    @Test
    fun readingProgress_neverDropsBelowZero() {
        // Arrange
        val book = bookOf(position = -10, size = 2048)

        // Act
        val percent = book.percent

        // Assert
        assertEquals(0f, percent, 0f)
    }

    @Test
    fun pageIsOneBasedAndCountedInKilobytes() {
        // Arrange
        val book = bookOf(position = 2048, size = 4096)

        // Act & Assert
        assertEquals(3, book.page)
        assertEquals(5, book.pages)
    }

    private fun bookOf(position: Int, size: Int) = Book(
        id = 1,
        title = "Title",
        author = "Author",
        size = size,
        position = position,
        readTime = 0,
        file = File("book.fb2"),
        cover = null,
    )
}
