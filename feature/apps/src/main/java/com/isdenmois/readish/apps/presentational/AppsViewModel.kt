package com.isdenmois.readish.apps.presentational

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isdenmois.readish.apps.model.APPS_PAGE_SIZE
import com.isdenmois.readish.apps.model.AppEntry
import com.isdenmois.readish.apps.repository.AppRepository
import com.isdenmois.readish.ui.AppLauncher
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.ReaderLauncher
import com.isdenmois.readish.ui.page
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel
@Inject
constructor(
    private val repository: AppRepository,
    private val appLauncher: AppLauncher,
    private val readerLauncher: ReaderLauncher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    fun loadApps() {
        _uiState.update { it.copy(apps = LoadState.Loading) }

        viewModelScope.launch {
            val apps = loadState { repository.getLauncherApps() }

            _uiState.update { state ->
                val page = (apps as? LoadState.Success)?.data?.page(state.page, APPS_PAGE_SIZE)?.page ?: state.page

                state.copy(apps = apps, page = page)
            }
        }
    }

    fun nextPage() = movePage(1)

    fun previousPage() = movePage(-1)

    fun openApp(app: AppEntry) {
        appLauncher.launch(app.packageName)
    }

    fun openOnyxHome() {
        readerLauncher.openOnyxHome()
    }

    private fun movePage(delta: Int) {
        _uiState.update { state ->
            val apps = (state.apps as? LoadState.Success)?.data ?: return@update state
            val page = apps.page(state.page, APPS_PAGE_SIZE)

            state.copy(page = (page.page + delta).coerceIn(0, page.pageCount - 1))
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
