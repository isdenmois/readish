package com.isdenmois.readish.library.repository

import android.content.Context
import android.content.SharedPreferences
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.library.lib.CacheManager
import com.isdenmois.readish.library.lib.EBookCache
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class LibraryRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var preferences: SharedPreferences
    private lateinit var cache: EBookCache
    private lateinit var repository: LibraryRepository

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        EBookParser.cacheDirectory = context.cacheDir
        preferences = context.getSharedPreferences("library-test", Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
        cache = EBookCache(CacheManager(preferences))
        repository = LibraryRepository(tempFolder.root, cache)
    }

    @Test
    fun getBooks_listsBookFilesNewestFirst() {
        // Arrange
        book("older.fb2", "Older").setLastModified(1_000)
        book("newer.fb2", "Newer").setLastModified(2_000)

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertEquals(listOf("newer.fb2", "older.fb2"), books.map { it.file.name })
        assertEquals("Newer", books.first().title)
        assertEquals(2_000, books.first().modifiedAt)
    }

    @Test
    fun getBooks_readsMetadataAndAuthor() {
        // Arrange
        book("dune.fb2", "Dune", author = "Frank Herbert")

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertEquals("Dune", books.single().title)
        assertEquals("Frank Herbert", books.single().author)
    }

    @Test
    fun getBooks_acceptsUppercaseExtensions() {
        // Arrange
        book("dune.FB2", "Dune")

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertEquals(listOf("dune.FB2"), books.map { it.file.name })
    }

    @Test
    fun getBooks_skipsHiddenFilesDirectoriesAndOtherFormats() {
        // Arrange
        book("dune.fb2", "Dune")
        book("notes.txt", "Ignored")
        book(".hidden.fb2", "Hidden")
        File(tempFolder.root, "nested").mkdir()
        book(File(tempFolder.root, "nested"), "nested.fb2", "Nested")
        File(tempFolder.root, "folder.fb2").mkdir()

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertEquals(listOf("dune.fb2"), books.map { it.file.name })
    }

    @Test
    fun getBooks_fallsBackToFileNameWhenBookCannotBeParsed() {
        // Arrange
        File(tempFolder.root, "broken.fb2").writeText("not a book")

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertEquals("broken", books.single().title)
        assertEquals("", books.single().author)
        assertNull(books.single().cover)
    }

    @Test
    fun getBooks_usesCachedMetadata() {
        // Arrange
        val file = book("cached.fb2", "Source title")
        cache.set(EBookFile(title = "Cached title", author = "Cached author", file = file, cover = null))

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertEquals("Cached title", books.single().title)
        assertEquals("Cached author", books.single().author)
    }

    @Test
    fun getBooks_returnsEmptyListForMissingDirectory() {
        // Arrange
        repository = LibraryRepository(File(tempFolder.root, "missing"), cache)

        // Act
        val books = runBlocking { repository.getBooks() }

        // Assert
        assertTrue(books.isEmpty())
    }

    @Test
    fun getLatest_returnsOnlyRequestedNewestFiles() {
        // Arrange
        book("oldest.fb2", "Oldest").setLastModified(1_000)
        book("middle.fb2", "Middle").setLastModified(2_000)
        book("newest.fb2", "Newest").setLastModified(3_000)

        // Act
        val books = runBlocking { repository.getLatest(2) }

        // Assert
        assertEquals(listOf("newest.fb2", "middle.fb2"), books.map { it.file.name })
    }

    private fun book(fileName: String, title: String, author: String = "Author"): File =
        File(tempFolder.root, fileName).apply { writeText(fb2(title, author)) }

    private fun book(folder: File, fileName: String, title: String): File =
        File(folder, fileName).apply { writeText(fb2(title, "Author")) }

    private fun fb2(title: String, author: String): String {
        val firstName = author.substringBefore(' ')
        val lastName = author.substringAfter(' ', "")

        return """<?xml version="1.0" encoding="utf-8"?>
        <FictionBook xmlns="http://www.gribuser.ru/xml/fictionbook/2.0">
          <description><title-info><book-title>$title</book-title>
          <author><first-name>$firstName</first-name><last-name>$lastName</last-name></author>
          </title-info></description>
          <body><section><p>Text</p></section></body>
        </FictionBook>"""
    }
}
