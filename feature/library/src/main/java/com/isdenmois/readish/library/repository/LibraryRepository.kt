package com.isdenmois.readish.library.repository

import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.library.di.BooksDir
import com.isdenmois.readish.library.lib.EBookCache
import com.isdenmois.readish.library.model.LibraryBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class LibraryRepository
@Inject
constructor(@BooksDir private val booksDir: File, private val cache: EBookCache) {
    suspend fun getBooks(): List<LibraryBook> = withContext(Dispatchers.Default) { bookFiles().map(::parse) }

    suspend fun getLatest(limit: Int): List<LibraryBook> = withContext(Dispatchers.Default) {
        bookFiles().take(limit).map(::parse)
    }

    private fun bookFiles(): List<File> = booksDir.listFiles()
        ?.filter { it.isFile && !it.isHidden && it.extension.lowercase() in BOOK_EXTENSIONS }
        ?.sortedByDescending { it.lastModified() }
        .orEmpty()

    private fun parse(file: File): LibraryBook {
        val modifiedAt = file.lastModified()

        cache[file]?.let { return LibraryBook.from(it, modifiedAt) }

        val parsed =
            try {
                EBookParser.parseBook(file)
            } catch (e: Exception) {
                null
            }

        cache.set(parsed)

        return parsed?.let { LibraryBook.from(it, modifiedAt) } ?: LibraryBook.unreadable(file)
    }

    private companion object {
        val BOOK_EXTENSIONS = setOf("fb2", "epub")
    }
}
