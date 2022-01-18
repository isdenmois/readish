package com.isdenmois.readish.home.repository

import android.content.Context
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.home.R
import com.isdenmois.readish.home.lib.EBookCache
import com.isdenmois.readish.home.model.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject

val bookProjection = arrayOf(
    RecentTable.Column.id,
    RecentTable.Column.title,
    RecentTable.Column.author,
    RecentTable.Column.bookSize,
    RecentTable.Column.position,
    RecentTable.Column.path,
    RecentTable.Column.readTime,
)

class BookRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cache: EBookCache,
) {
    suspend fun getCurrentBooks(limit: Int = 4): List<Book> = withContext(
        Dispatchers.Default
    ) {
        val cursor = context.contentResolver.query(
            RecentTable.URI,
            bookProjection,
            null,
            null,
            "${RecentTable.Column.updatedAt} DESC LIMIT $limit",
            null,
        )
        val result = mutableListOf<Book>()

        cursor?.use {
            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                val file = CursorManager.getFile(cursor, RecentTable.Column.path)
                val parsed = parseFile(file)

                result.add(
                    Book(
                        id = CursorManager.getInt(cursor, RecentTable.Column.id),
                        title = CursorManager.getString(cursor, RecentTable.Column.title),
                        author = CursorManager.getString(cursor, RecentTable.Column.author),
                        size = CursorManager.getInt(cursor, RecentTable.Column.bookSize),
                        position = CursorManager.getInt(cursor, RecentTable.Column.position),
                        file = file,
                        readTime = CursorManager.getInt(cursor, RecentTable.Column.readTime),
                        cover = parsed?.cover,
                    )
                )

                cursor.moveToNext()
            }
        }

        return@withContext result
    }

    suspend fun getLatestAddedBooks(limit: Int = 6): List<EBookFile> = withContext(
        Dispatchers.Default
    ) {
        val files = File(context.getString(R.string.books_dir)).listFiles()

        val bookList = files
            ?.filter { it.name.endsWith("fb2") || it.name.endsWith("epub") }
            ?.sortedByDescending { it.lastModified() }
            ?.take(limit)
            ?.mapNotNull { parseFile(it) }
            ?.toList() ?: listOf()


        return@withContext bookList
    }

    private fun parseFile(file: File): EBookFile? {
        var book = cache[file]

        if (book == null) {
            book = EBookParser.parseBook(file)
            cache.set(book)
        }

        return book
    }
}
