package com.isdenmois.readish.home.presentational

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isdenmois.ebookparser.EBookParser
import com.isdenmois.readish.home.R
import com.isdenmois.readish.home.model.LoadState
import com.isdenmois.readish.home.model.MimeUtils
import com.isdenmois.readish.home.repository.BookRepository
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
        val packageManager = applicationContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage("com.neverland.alreaderprofs")

        applicationContext.startActivity(intent)
    }

    fun openBook(file: File) {
        val packageManager = applicationContext.packageManager
        val uri = Uri.fromFile(file)
        val type = MimeUtils.getByFileName(file.name)

        val intent =
            packageManager.getLaunchIntentForPackage("com.neverland.alreaderprofs")?.apply {
                setDataAndType(uri, type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        try {
            applicationContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.cant_start_application),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun openOnyxHome() {
        val intent =
            Intent().apply {
                component = ComponentName("com.onyx", "com.onyx.reader.main.ui.MainActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        try {
            applicationContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.cant_start_application),
                Toast.LENGTH_SHORT,
            ).show()
        }
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
