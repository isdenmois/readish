package com.isdenmois.readish.home.presentational

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.home.R
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.presentational.ui.BookFileItem
import com.isdenmois.readish.home.presentational.ui.CurrentBook
import com.isdenmois.readish.home.presentational.ui.LatestBook
import com.isdenmois.readish.home.presentational.ui.LoadStateContent
import com.isdenmois.readish.ui.Icon
import com.isdenmois.readish.ui.noRippleClickable
import java.io.File

@Composable
fun HomeScreen(onOpenTransfers: () -> Unit, vm: HomeViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        vm.loadBooks()

        onPauseOrDispose { }
    }

    HomeContent(
        state = state,
        onOpenTransfers = onOpenTransfers,
        onOpenCurrentBook = vm::openAlReader,
        onOpenBook = vm::openBook,
        onShowBrightness = vm::showSystemBrightnessDialog,
        onOpenOnyxHome = vm::openOnyxHome,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onOpenTransfers: () -> Unit,
    onOpenCurrentBook: () -> Unit,
    onOpenBook: (File) -> Unit,
    onShowBrightness: () -> Unit,
    onOpenOnyxHome: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.my_books),
                style = MaterialTheme.typography.displaySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                title = stringResource(R.string.brightness),
                icon = R.drawable.ic_lightbulb,
                onClick = onShowBrightness,
            )

            Icon(
                title = stringResource(R.string.applications),
                icon = R.drawable.ic_apps,
                isLast = true,
                onClick = onOpenOnyxHome,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LoadStateContent(state.books) { books ->
            if (books.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_books),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                MyBooks(books, onOpenCurrentBook, onOpenBook)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.noRippleClickable { onOpenTransfers() }) {
            Text(
                text = stringResource(R.string.latest_added),
                style = MaterialTheme.typography.displaySmall,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoadStateContent(state.latest) { files ->
            LatestAdded(files, onOpenBook)
        }
    }
}

@Composable
private fun LatestAdded(files: List<EBookFile>, onOpenBook: (File) -> Unit) {
    LazyRow {
        itemsIndexed(files) { index, item ->
            if (index != 0) {
                Spacer(modifier = Modifier.width(24.dp))
            }

            BookFileItem(item, onClick = { onOpenBook(item.file) })
        }
    }
}

@Composable
fun MyBooks(books: List<Book>, onOpenCurrentBook: () -> Unit, onOpenBook: (File) -> Unit) {
    if (books.isEmpty()) {
        return
    }

    val current = books[0]
    val others = books.drop(1)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .border(0.5.dp, MaterialTheme.colorScheme.outline)
                    .padding(24.dp),
            ) {
                CurrentBook(book = current, onClick = onOpenCurrentBook)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp),
        ) {
            itemsIndexed(others) { index, book ->
                if (index != 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                LatestBook(book, onClick = { onOpenBook(book.file) })
            }
        }
    }
}
