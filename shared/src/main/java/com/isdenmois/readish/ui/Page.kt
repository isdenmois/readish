package com.isdenmois.readish.ui

data class Page<T>(val items: List<T>, val page: Int, val pageCount: Int)

fun <T> List<T>.page(page: Int, pageSize: Int): Page<T> {
    val pageCount = maxOf(1, (size + pageSize - 1) / pageSize)
    val current = page.coerceIn(0, pageCount - 1)

    return Page(
        items = drop(current * pageSize).take(pageSize),
        page = current,
        pageCount = pageCount,
    )
}
