package com.isdenmois.readish.apps.presentational

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isdenmois.readish.apps.R
import com.isdenmois.readish.apps.model.APPS_PAGE_SIZE
import com.isdenmois.readish.apps.model.AppEntry
import com.isdenmois.readish.apps.presentational.ui.AppItem
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.Page
import com.isdenmois.readish.ui.page

@Composable
fun AppsScreen(onBack: () -> Unit, vm: AppsViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        vm.loadApps()

        onPauseOrDispose { }
    }

    AppsContent(
        state = state,
        onBack = onBack,
        onOpenOnyxHome = vm::openOnyxHome,
        onOpenApp = vm::openApp,
        onPreviousPage = vm::previousPage,
        onNextPage = vm::nextPage,
    )
}

@Composable
fun AppsContent(
    state: AppsUiState,
    onBack: () -> Unit,
    onOpenOnyxHome: () -> Unit,
    onOpenApp: (AppEntry) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
) {
    val apps = (state.apps as? LoadState.Success)?.data
    val view = remember(apps, state.page) { apps?.page(state.page, APPS_PAGE_SIZE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.applications),
                style = MaterialTheme.typography.displaySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onOpenOnyxHome) {
                Text(stringResource(R.string.onyx_home))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when (val load = state.apps) {
                LoadState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is LoadState.Error ->
                    Text(
                        text = load.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )

                is LoadState.Success -> view?.let { Grid(load.data, it, onOpenApp) }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Pager(view, onPreviousPage, onNextPage)
    }
}

@Composable
private fun Grid(apps: List<AppEntry>, view: Page<AppEntry>, onOpenApp: (AppEntry) -> Unit) {
    if (apps.isEmpty()) {
        Text(
            text = stringResource(R.string.no_applications),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 48.dp),
        )

        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        view.items.chunked(GRID_COLUMNS).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { app ->
                    AppItem(
                        app = app,
                        onClick = { onOpenApp(app) },
                        modifier = Modifier.weight(1f),
                    )
                }

                repeat(GRID_COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Pager(view: Page<AppEntry>?, onPreviousPage: () -> Unit, onNextPage: () -> Unit) {
    val page = view?.page ?: 0
    val pageCount = view?.pageCount ?: 1

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextButton(onClick = onPreviousPage, enabled = page > 0) {
            Text(stringResource(R.string.previous_page))
        }

        Text(
            text = stringResource(R.string.page_indicator, page + 1, pageCount),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        TextButton(onClick = onNextPage, enabled = page < pageCount - 1) {
            Text(stringResource(R.string.next_page))
        }
    }
}

private const val GRID_COLUMNS = 5
