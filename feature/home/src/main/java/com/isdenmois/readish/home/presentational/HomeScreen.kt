package com.isdenmois.readish.home.presentational

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.home.R
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.home.presentational.ui.BookFileItem
import com.isdenmois.readish.home.presentational.ui.CurrentBook
import com.isdenmois.readish.home.presentational.ui.LatestBook
import com.isdenmois.readish.home.presentational.ui.ResourceLoading
import com.isdenmois.readish.ui.Icon
import com.isdenmois.readish.ui.noRippleClickable

@Composable
fun HomeScreen() {
    val vm: HomeViewModel = viewModel()
    val bookList by vm.bookList
    val fileList by vm.fileList

    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)) {
        Row {
            Text("My Books", fontSize = 32.sp)

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                title = "Brightness",
                icon = R.drawable.ic_lightbulb,
                onClick = { vm.showSystemBrightnessDialog() }
            )

            Icon(
                title = "Applications",
                icon = R.drawable.ic_apps,
                isLast = true,
                onClick = { vm.openOnyxHome() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ResourceLoading(resource = bookList) { books ->
            MyBooks(books, vm)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.noRippleClickable { vm.openTransfers() }) {
            Text("Latest added", fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ResourceLoading(resource = fileList) { files ->
            LatestAdded(files, vm)
        }
    }
}

@Composable
private fun LatestAdded(files: List<EBookFile>, vm: HomeViewModel) {
    LazyRow {
        itemsIndexed(files) { index, item ->
            if (index != 0) {
                Spacer(modifier = Modifier.width(24.dp))
            }

            BookFileItem(item, onClick = { vm.openBook(item.file) })
        }
    }
}

@Composable
fun MyBooks(books: List<Book>, vm: HomeViewModel) {
    if (books.isEmpty()) {
        return
    }

    val current = books[0]
    val others = books.drop(1)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .border(0.5.dp, Color.Gray)
                    .padding(24.dp)
            ) {
                CurrentBook(book = current, onClick = { vm.openAlReader() })
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp)
        ) {
            itemsIndexed(others) { index, book ->
                if (index != 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                LatestBook(book, onClick = { vm.openBook(book.file) })
            }
        }
    }
}
