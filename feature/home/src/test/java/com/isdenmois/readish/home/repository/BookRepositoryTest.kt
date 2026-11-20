package com.isdenmois.readish.home.repository

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.library.lib.CacheManager
import com.isdenmois.readish.library.lib.EBookCache
import com.isdenmois.readish.library.repository.LibraryRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class BookRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var repository: BookRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        EBookParser.cacheDirectory = context.cacheDir
        val preferences = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        val cache = EBookCache(CacheManager(preferences))
        repository = BookRepository(context, cache, LibraryRepository(tempFolder.root, cache))
    }

    @Test
    fun getCurrentBooks_mapsReaderRowsToBooks() {
        // Arrange
        val bookFile = tempFolder.newFile("dune.fb2").apply { writeText(fb2("Dune", "Frank", "Herbert")) }
        providerReturning(
            cursorOf(arrayOf(7, "Dune", "Frank Herbert", 4096, 1024, bookFile.absolutePath, 90L)),
        )

        // Act
        val books = runBlocking { repository.getCurrentBooks() }

        // Assert
        assertEquals(1, books.size)
        books.first().let { book ->
            assertEquals(7, book.id)
            assertEquals("Dune", book.title)
            assertEquals("Frank Herbert", book.author)
            assertEquals(4096, book.size)
            assertEquals(1024, book.position)
            assertEquals(90, book.readTime)
            assertEquals(bookFile, book.file)
        }
    }

    @Test
    fun getCurrentBooks_asksReaderForMostRecentlyReadBooks() {
        // Arrange
        val provider = providerReturning(cursorOf())

        // Act
        runBlocking { repository.getCurrentBooks() }

        // Assert
        assertEquals("datelast DESC LIMIT 4", provider.sortOrders.single())
    }

    @Test
    fun getCurrentBooks_returnsEmptyListWhenProviderHasNoRows() {
        // Arrange
        val provider = providerReturning(cursorOf())

        // Act
        val books = runBlocking { repository.getCurrentBooks() }

        // Assert
        assertTrue(books.isEmpty())
    }

    @Test
    fun getCurrentBooks_survivesUnreadableBookFile() {
        // Arrange
        val corrupt = tempFolder.newFile("corrupt.fb2").apply { writeText("not a book") }
        providerReturning(cursorOf(arrayOf(1, "Corrupt", "Unknown", 10, 0, corrupt.absolutePath, 0L)))

        // Act
        val books = runBlocking { repository.getCurrentBooks() }

        // Assert
        assertEquals(1, books.size)
        assertEquals("Corrupt", books.single().title)
        assertNull(books.single().cover)
    }

    private fun providerReturning(cursor: Cursor): FakeRecentProvider =
        Robolectric.setupContentProvider(FakeRecentProvider::class.java, AUTHORITY).also { it.cursor = cursor }

    private fun cursorOf(vararg rows: Array<Any?>): Cursor = MatrixCursor(COLUMNS).apply {
        rows.forEach { addRow(it) }
    }

    private fun fb2(title: String, firstName: String, lastName: String) = """<?xml version="1.0" encoding="utf-8"?>
        <FictionBook xmlns="http://www.gribuser.ru/xml/fictionbook/2.0">
          <description><title-info><book-title>$title</book-title>
          <author><first-name>$firstName</first-name><last-name>$lastName</last-name></author>
          </title-info></description>
          <body><section><p>Text</p></section></body>
        </FictionBook>"""

    private class FakeRecentProvider : ContentProvider() {
        lateinit var cursor: Cursor
        val sortOrders = mutableListOf<String>()

        override fun onCreate() = true

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?,
        ): Cursor {
            sortOrders.add(sortOrder ?: "")
            return cursor
        }

        override fun getType(uri: Uri): String? = null

        override fun insert(uri: Uri, values: ContentValues?): Uri? = null

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

        override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
    }

    private companion object {
        const val AUTHORITY = "com.neverland.alreaderprofs.BooksProvider"

        val COLUMNS =
            arrayOf(
                RecentTable.Column.ID,
                RecentTable.Column.TITLE,
                RecentTable.Column.AUTHOR,
                RecentTable.Column.BOOK_SIZE,
                RecentTable.Column.POSITION,
                RecentTable.Column.PATH,
                RecentTable.Column.READ_TIME,
            )
    }
}
