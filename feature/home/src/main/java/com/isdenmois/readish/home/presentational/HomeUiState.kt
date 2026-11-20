package com.isdenmois.readish.home.presentational

import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.model.LoadState

data class HomeUiState(
    val books: LoadState<List<Book>> = LoadState.Loading,
    val latest: LoadState<List<EBookFile>> = LoadState.Loading,
)
