package com.isdenmois.readish.library.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class LibraryPaginationTest {
    @Test
    fun view_returnsTwelveBooksPerPage() {
        // Arrange
        val books = (1..13).map { book("Book $it", "book-$it.fb2") }

        // Act
        val first = books.view(LibraryQuery(), page = 0)

        // Assert
        assertEquals(12, first.books.size)
        assertEquals(2, first.pageCount)
        assertEquals(0, first.page)
    }

    @Test
    fun view_returnsRemainderOnLastPage() {
        // Arrange
        val books = (1..13).map { book("Book $it", "book-$it.fb2") }

        // Act
        val last = books.view(LibraryQuery(), page = 1)

        // Assert
        assertEquals(1, last.books.size)
        assertEquals(1, last.page)
    }

    @Test
    fun view_clampsPageAboveLastPage() {
        // Arrange
        val books = (1..13).map { book("Book $it", "book-$it.fb2") }

        // Act
        val view = books.view(LibraryQuery(), page = 99)

        // Assert
        assertEquals(1, view.page)
    }

    @Test
    fun view_clampsNegativePage() {
        // Arrange
        val books = (1..13).map { book("Book $it", "book-$it.fb2") }

        // Act
        val view = books.view(LibraryQuery(), page = -3)

        // Assert
        assertEquals(0, view.page)
    }

    @Test
    fun view_ofEmptyList_isSingleEmptyPage() {
        // Act
        val view = emptyList<LibraryBook>().view(LibraryQuery(), page = 4)

        // Assert
        assertEquals(emptyList<LibraryBook>(), view.books)
        assertEquals(0, view.page)
        assertEquals(1, view.pageCount)
    }

    @Test
    fun view_appliesFilterBeforePaging() {
        // Arrange
        val books = (1..13).map { book("Book $it", "book-$it.fb2") }

        // Act
        val view = books.view(LibraryQuery(filter = "Book 1"), page = 0)

        // Assert
        assertEquals(5, view.books.size)
        assertEquals(1, view.pageCount)
    }

    @Test
    fun view_sortsBeforePaging() {
        // Arrange
        val books = (1..13).map { book("Book $it", "book-$it.fb2", modified = it.toLong()) }

        // Act
        val firstPage = books.view(LibraryQuery(), page = 0)

        // Assert
        assertEquals("book-13.fb2", firstPage.books.first().file.name)
    }

    private fun book(title: String, fileName: String, modified: Long = 0) = LibraryBook(
        file = File(fileName),
        title = title,
        author = "Author",
        cover = null,
        modifiedAt = modified,
    )
}
