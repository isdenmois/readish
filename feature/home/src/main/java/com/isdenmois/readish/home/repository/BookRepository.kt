package com.isdenmois.readish.home.repository

import android.content.Context
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.home.lib.forEach
import com.isdenmois.readish.home.lib.getFile
import com.isdenmois.readish.home.lib.getInt
import com.isdenmois.readish.home.lib.getString
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.library.lib.EBookCache
import com.isdenmois.readish.library.model.toEBookFile
import com.isdenmois.readish.library.repository.LibraryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class BookRepository
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val cache: EBookCache,
    private val libraryRepository: LibraryRepository,
) {
    suspend fun getCurrentBooks(limit: Int = 4): List<Book> = withContext(
        Dispatchers.Default,
    ) {
        val cursor =
            context.contentResolver.query(
                RecentTable.URI,
                bookProjection,
                null,
                null,
                "${RecentTable.Column.UPDATED_AT} DESC LIMIT $limit",
                null,
            )
        val result = mutableListOf<Book>()

        cursor?.use {
            it.forEach {
                val file = getFile(RecentTable.Column.PATH)
                val parsed = parseFile(file)

                result.add(
                    Book(
                        id = getInt(RecentTable.Column.ID),
                        title = getString(RecentTable.Column.TITLE),
                        author = getString(RecentTable.Column.AUTHOR),
                        size = getInt(RecentTable.Column.BOOK_SIZE),
                        position = getInt(RecentTable.Column.POSITION),
                        file = file,
                        readTime = getInt(RecentTable.Column.READ_TIME),
                        cover = parsed?.cover,
                    ),
                )
            }
        }

        return@withContext result
    }

    suspend fun getLatestAddedBooks(limit: Int = 6): List<EBookFile> =
        libraryRepository.getLatest(limit).map { it.toEBookFile() }

    private fun parseFile(file: File): EBookFile? {
        var book = cache[file]

        if (book == null) {
            book =
                try {
                    EBookParser.parseBook(file)
                } catch (e: Exception) {
                    null
                }
            cache.set(book)
        }

        return book
    }

    companion object {
        private val bookProjection =
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
