package com.isdenmois.readish.library.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class LibraryQueryTest {
    @Test
    fun defaultQuery_sortsByTimeNewestFirst() {
        // Arrange
        val books = listOf(book("Old", "old.fb2", modified = 10), book("New", "new.fb2", modified = 20))

        // Act
        val sorted = books.applyQuery(LibraryQuery())

        // Assert
        assertEquals(listOf("new.fb2", "old.fb2"), sorted.map { it.file.name })
    }

    @Test
    fun timeAscending_sortsOldestFirst() {
        // Arrange
        val books = listOf(book("Old", "old.fb2", modified = 10), book("New", "new.fb2", modified = 20))

        // Act
        val sorted = books.applyQuery(LibraryQuery(sort = SortField.TIME, ascending = true))

        // Assert
        assertEquals(listOf("old.fb2", "new.fb2"), sorted.map { it.file.name })
    }

    @Test
    fun titleSort_isCaseInsensitive() {
        // Arrange
        val books = listOf(book("zebra", "z.fb2"), book("Apple", "a.fb2"), book("mango", "m.fb2"))

        // Act
        val sorted = books.applyQuery(LibraryQuery(sort = SortField.TITLE, ascending = true))

        // Assert
        assertEquals(listOf("Apple", "mango", "zebra"), sorted.map { it.title })
    }

    @Test
    fun filenameSort_usesFileNameInsteadOfTitle() {
        // Arrange
        val books = listOf(book("Zeta", "a.fb2"), book("Alpha", "z.fb2"))

        // Act
        val sorted = books.applyQuery(LibraryQuery(sort = SortField.FILENAME, ascending = true))

        // Assert
        assertEquals(listOf("a.fb2", "z.fb2"), sorted.map { it.file.name })
    }

    @Test
    fun titleSortDescending_reversesOrder() {
        // Arrange
        val books = listOf(book("Apple", "a.fb2"), book("Zebra", "z.fb2"))

        // Act
        val sorted = books.applyQuery(LibraryQuery(sort = SortField.TITLE, ascending = false))

        // Assert
        assertEquals(listOf("Zebra", "Apple"), sorted.map { it.title })
    }

    @Test
    fun filter_matchesTitleCaseInsensitive() {
        // Arrange
        val books = listOf(book("Dune", "dune.fb2"), book("Solaris", "solaris.fb2"))

        // Act
        val filtered = books.applyQuery(LibraryQuery(filter = "dUN"))

        // Assert
        assertEquals(listOf("Dune"), filtered.map { it.title })
    }

    @Test
    fun filter_matchesFileName() {
        // Arrange
        val books = listOf(book("Dune", "dune.fb2"), book("Solaris", "lem-solaris.epub"))

        // Act
        val filtered = books.applyQuery(LibraryQuery(filter = "lem-"))

        // Assert
        assertEquals(listOf("Solaris"), filtered.map { it.title })
    }

    @Test
    fun filter_ignoresSurroundingSpaces() {
        // Arrange
        val books = listOf(book("Dune", "dune.fb2"))

        // Act
        val filtered = books.applyQuery(LibraryQuery(filter = "  dune  "))

        // Assert
        assertEquals(1, filtered.size)
    }

    @Test
    fun filter_withNoMatches_returnsEmptyList() {
        // Arrange
        val books = listOf(book("Dune", "dune.fb2"))

        // Act
        val filtered = books.applyQuery(LibraryQuery(filter = "nothing"))

        // Assert
        assertEquals(emptyList<LibraryBook>(), filtered)
    }

    @Test
    fun applyQuery_keepsBookWhoseTitleAndFileNameBothMatchOnlyOnce() {
        // Arrange
        val books = listOf(book("Dune", "dune.fb2"))

        // Act
        val filtered = books.applyQuery(LibraryQuery(filter = "dune"))

        // Assert
        assertEquals(1, filtered.size)
    }

    private fun book(title: String, fileName: String, modified: Long = 0, author: String = "Author") = LibraryBook(
        file = File(fileName),
        title = title,
        author = author,
        cover = null,
        modifiedAt = modified,
    )
}
