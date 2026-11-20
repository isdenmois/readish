package com.isdenmois.readish.library.presentational

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isdenmois.readish.library.R
import com.isdenmois.readish.library.model.LibraryBook
import com.isdenmois.readish.library.model.SortField
import com.isdenmois.readish.library.model.view
import com.isdenmois.readish.library.presentational.ui.LibraryBookItem
import com.isdenmois.readish.ui.LoadState
import com.isdenmois.readish.ui.Page

@Composable
fun LibraryScreen(onBack: () -> Unit, vm: LibraryViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        vm.loadBooks()

        onPauseOrDispose { }
    }

    LibraryContent(
        state = state,
        onBack = onBack,
        onOpenBook = vm::openBook,
        onSort = vm::setSort,
        onFilter = vm::setFilter,
        onPreviousPage = vm::previousPage,
        onNextPage = vm::nextPage,
    )
}

@Composable
fun LibraryContent(
    state: LibraryUiState,
    onBack: () -> Unit,
    onOpenBook: (LibraryBook) -> Unit,
    onSort: (SortField) -> Unit,
    onFilter: (String) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
) {
    val books = (state.books as? LoadState.Success)?.data
    val view = remember(books, state.query, state.page) { books?.view(state.query, state.page) }

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
                text = stringResource(R.string.all_books),
                style = MaterialTheme.typography.displaySmall,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Controls(state, onSort, onFilter)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when (val load = state.books) {
                LoadState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is LoadState.Error ->
                    Text(
                        text = load.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )

                is LoadState.Success -> view?.let { Grid(load.data, it, onOpenBook) }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Pager(view, onPreviousPage, onNextPage)
    }
}

@Composable
private fun Controls(state: LibraryUiState, onSort: (SortField) -> Unit, onFilter: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SortField.entries.forEach { field ->
            val selected = field == state.sort
            val label = stringResource(field.labelRes)

            FilterChip(
                selected = selected,
                onClick = { onSort(field) },
                label = {
                    Text(
                        text = if (selected) "$label ${if (state.ascending) "↑" else "↓"}" else label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    )
                },
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        OutlinedTextField(
            value = state.filter,
            onValueChange = onFilter,
            singleLine = true,
            placeholder = { Text(stringResource(R.string.filter_hint)) },
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        )
    }
}

@Composable
private fun Grid(books: List<LibraryBook>, view: Page<LibraryBook>, onOpenBook: (LibraryBook) -> Unit) {
    when {
        books.isEmpty() -> EmptyState(stringResource(R.string.no_books), hint = null)

        view.items.isEmpty() ->
            EmptyState(
                title = stringResource(R.string.no_matches),
                hint = stringResource(R.string.no_matches_hint),
            )

        else ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                view.items.chunked(GRID_COLUMNS).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        row.forEach { book ->
                            LibraryBookItem(
                                book = book,
                                onClick = { onOpenBook(book) },
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
}

@Composable
private fun EmptyState(title: String, hint: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (hint != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Pager(view: Page<LibraryBook>?, onPreviousPage: () -> Unit, onNextPage: () -> Unit) {
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

private val SortField.labelRes: Int
    get() =
        when (this) {
            SortField.TIME -> R.string.sort_time
            SortField.TITLE -> R.string.sort_title
            SortField.FILENAME -> R.string.sort_filename
        }

private const val GRID_COLUMNS = 4
