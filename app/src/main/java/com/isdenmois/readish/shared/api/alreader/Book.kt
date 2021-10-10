package com.isdenmois.readish.shared.api.alreader

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val size: Int,
    val position: Int,
    val readTime: Int,
    val path: String,
) {
    val percent = position.toFloat() / size
    val page = position / 1024 + 1
    val pages = size / 1024 + 1
}
