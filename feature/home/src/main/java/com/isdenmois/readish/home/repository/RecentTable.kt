package com.isdenmois.readish.home.repository

import android.net.Uri

object RecentTable {
    val URI: Uri = Uri.parse("content://com.neverland.alreaderprofs.BooksProvider/recent")

    object Column {
        const val ID = "id"
        const val TITLE = "title"
        const val AUTHOR = "author"
        const val BOOK_SIZE = "booksize"
        const val POSITION = "bookpos"
        const val READ_TIME = "param0"
        const val PATH = "filename"
        const val UPDATED_AT = "datelast"
    }
}
