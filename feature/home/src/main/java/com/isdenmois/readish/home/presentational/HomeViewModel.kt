package com.isdenmois.readish.home.presentational

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.home.repository.BookRepository
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    @ApplicationContext private val applicationContext: Context,
    private val repository: BookRepository,
    private val readerLauncher: ReaderLauncher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        EBookParser.cacheDirectory = applicationContext.cacheDir
    }

    fun loadBooks() {
        _uiState.value = HomeUiState()

        viewModelScope.launch {
            val books = loadState { repository.getCurrentBooks() }

            _uiState.update { it.copy(books = books) }
        }

        viewModelScope.launch {
            val latest = loadState { repository.getLatestAddedBooks() }

            _uiState.update { it.copy(latest = latest) }
        }
    }

    fun openAlReader() {
        readerLauncher.openReader()
    }

    fun openBook(file: File) {
        readerLauncher.openBook(file)
    }

    fun showSystemBrightnessDialog() {
        try {
            val intent = Intent("action.show.brightness.dialog")

            applicationContext.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
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
