package com.isdenmois.readish.library.model

enum class SortField { TIME, TITLE, FILENAME }

data class LibraryQuery(val sort: SortField = SortField.TIME, val ascending: Boolean = false, val filter: String = "")

const val LIBRARY_PAGE_SIZE = 12

data class LibraryView(val books: List<LibraryBook>, val page: Int, val pageCount: Int)

fun List<LibraryBook>.applyQuery(query: LibraryQuery): List<LibraryBook> {
    val needle = query.filter.trim()
    val matches =
        if (needle.isEmpty()) {
            this
        } else {
            filter { it.title.contains(needle, ignoreCase = true) || it.file.name.contains(needle, ignoreCase = true) }
        }

    val byFileName = compareBy<LibraryBook, String>(String.CASE_INSENSITIVE_ORDER) { it.file.name }
    val comparator: Comparator<LibraryBook> =
        when (query.sort) {
            SortField.TIME -> compareBy { it.modifiedAt }
            SortField.TITLE -> compareBy<LibraryBook, String>(String.CASE_INSENSITIVE_ORDER) { it.title }
            SortField.FILENAME -> byFileName
        }

    val directed = if (query.ascending) comparator else comparator.reversed()

    return matches.sortedWith(directed.then(byFileName))
}

fun List<LibraryBook>.view(query: LibraryQuery, page: Int, pageSize: Int = LIBRARY_PAGE_SIZE): LibraryView {
    val matches = applyQuery(query)
    val pageCount = maxOf(1, (matches.size + pageSize - 1) / pageSize)
    val current = page.coerceIn(0, pageCount - 1)

    return LibraryView(
        books = matches.drop(current * pageSize).take(pageSize),
        page = current,
        pageCount = pageCount,
    )
}
