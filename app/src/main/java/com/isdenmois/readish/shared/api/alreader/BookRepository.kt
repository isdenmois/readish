package com.isdenmois.readish.shared.api.alreader

import android.content.Context
import com.isdenmois.readish.R
import com.isdenmois.readish.shared.api.parser.BookFile
import com.isdenmois.readish.shared.api.parser.BookParser
import java.io.File
import java.util.*

val bookProjection = arrayOf(
    RecentTable.Column.id,
    RecentTable.Column.title,
    RecentTable.Column.author,
    RecentTable.Column.bookSize,
    RecentTable.Column.position,
    RecentTable.Column.path,
    RecentTable.Column.readTime,
)

class BookRepository(private val context: Context) {
    suspend fun getCurrentBooks(limit: Int = 4): List<Book> {
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
                val path = CursorManager.getPath(cursor, RecentTable.Column.path)
                val parsed = parseFile(path)

                result.add(
                    Book(
                        id = CursorManager.getInt(cursor, RecentTable.Column.id),
                        title = CursorManager.getString(cursor, RecentTable.Column.title),
                        author = CursorManager.getString(cursor, RecentTable.Column.author),
                        size = CursorManager.getInt(cursor, RecentTable.Column.bookSize),
                        position = CursorManager.getInt(cursor, RecentTable.Column.position),
                        path = path,
                        readTime = CursorManager.getInt(cursor, RecentTable.Column.readTime),
                        cover = parsed?.cover,
                    )
                )

                cursor.moveToNext()
            }
        }

        return result
    }

    suspend fun getLatestAddedBooks(limit: Int = 6): List<BookFile> {
        val files = File(context.getString(R.string.books_dir)).listFiles()

        return files
            ?.filter { it.name.endsWith("fb2") || it.name.endsWith("epub") }
            ?.sortedByDescending { it.lastModified() }
            ?.take(limit)
            ?.mapNotNull { parseFile(it.path) }
            ?.toList() ?: listOf()
    }

    private val parsed = WeakHashMap<String, BookFile?>()
    private fun parseFile(path: String): BookFile? {
        if (parsed.containsKey(path)){
            return parsed[path]
        }

        val data = BookParser.getParser(path).parse()

        parsed[path] = data

        return data
    }
}
