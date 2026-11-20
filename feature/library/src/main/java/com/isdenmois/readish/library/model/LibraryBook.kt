package com.isdenmois.readish.library.model

import android.graphics.Bitmap
import com.isdenmois.ebookparser.EBookFile
import java.io.File

data class LibraryBook(
    val file: File,
    val title: String,
    val author: String,
    val cover: Bitmap?,
    val modifiedAt: Long,
) {
    val subtitle: String get() = author.ifBlank { file.name }

    companion object {
        fun from(book: EBookFile, modifiedAt: Long) = LibraryBook(
            file = book.file,
            title = book.title.ifBlank { book.file.nameWithoutExtension },
            author = book.author.orEmpty(),
            cover = book.cover,
            modifiedAt = modifiedAt,
        )

        fun unreadable(file: File) = LibraryBook(
            file = file,
            title = file.nameWithoutExtension,
            author = "",
            cover = null,
            modifiedAt = file.lastModified(),
        )
    }
}

fun LibraryBook.toEBookFile() = EBookFile(title = title, author = author, cover = cover, file = file)
