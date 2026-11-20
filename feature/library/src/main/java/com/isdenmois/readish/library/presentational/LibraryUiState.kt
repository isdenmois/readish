package com.isdenmois.readish.library.presentational

import com.isdenmois.readish.library.model.LibraryBook
import com.isdenmois.readish.library.model.LibraryQuery
import com.isdenmois.readish.library.model.SortField
import com.isdenmois.readish.ui.LoadState

data class LibraryUiState(
    val books: LoadState<List<LibraryBook>> = LoadState.Loading,
    val sort: SortField = SortField.TIME,
    val ascending: Boolean = false,
    val filter: String = "",
    val page: Int = 0,
) {
    val query: LibraryQuery get() = LibraryQuery(sort = sort, ascending = ascending, filter = filter)
}
