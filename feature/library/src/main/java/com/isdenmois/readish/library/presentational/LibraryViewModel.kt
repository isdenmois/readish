package com.isdenmois.readish.library.presentational

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.library.model.LibraryBook
import com.isdenmois.readish.library.model.SortField
import com.isdenmois.readish.library.model.view
import com.isdenmois.readish.library.repository.LibraryRepository
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.ReaderLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
@Inject
constructor(
    @ApplicationContext private val applicationContext: Context,
    private val repository: LibraryRepository,
    private val readerLauncher: ReaderLauncher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        EBookParser.cacheDirectory = applicationContext.cacheDir
    }

    fun loadBooks() {
        _uiState.update { it.copy(books = LoadState.Loading, page = 0) }

        viewModelScope.launch {
            val books = loadState { repository.getBooks() }

            _uiState.update { it.copy(books = books) }
        }
    }

    fun setSort(field: SortField) {
        _uiState.update { state ->
            val ascending = if (state.sort == field) !state.ascending else field != SortField.TIME

            state.copy(sort = field, ascending = ascending, page = 0)
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(filter = filter, page = 0) }
    }

    fun nextPage() = movePage(1)

    fun previousPage() = movePage(-1)

    fun openBook(book: LibraryBook) {
        readerLauncher.openBook(book.file)
    }

    private fun movePage(delta: Int) {
        _uiState.update { state ->
            val books = (state.books as? LoadState.Success)?.data ?: return@update state
            val view = books.view(state.query, state.page)

            state.copy(page = (view.page + delta).coerceIn(0, view.pageCount - 1))
        }
    }

    private suspend fun <T> loadState(block: suspend () -> T): LoadState<T> = try {
        LoadState.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LoadState.Error(e.message ?: "Something went wrong")
    }
}
