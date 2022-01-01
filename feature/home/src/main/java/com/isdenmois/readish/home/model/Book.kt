package com.isdenmois.readish.home.model

import android.graphics.Bitmap
import java.io.File

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val size: Int,
    val position: Int,
    val readTime: Int,
    val file: File,
    val cover: Bitmap?,
) {
    val percent = position.toFloat() / size
    val page = position / 1024 + 1
    val pages = size / 1024 + 1
}
