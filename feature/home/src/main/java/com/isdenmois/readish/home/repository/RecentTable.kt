package com.isdenmois.readish.home.repository

import android.net.Uri

object RecentTable {
    val URI = Uri.parse("content://com.neverland.alreaderprofs.BooksProvider/recent")

    object Column {
        const val id = "id"
        const val title = "title"
        const val author = "author"
        const val bookSize = "booksize"
        const val position = "bookpos"
        const val readTime = "param0"
        const val path = "filename"
        const val updatedAt = "datelast"
    }
}
