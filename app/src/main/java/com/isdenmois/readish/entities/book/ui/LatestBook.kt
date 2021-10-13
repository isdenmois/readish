package com.isdenmois.readish.entities.book.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isdenmois.readish.shared.ui.EbookCover
import com.isdenmois.readish.shared.api.alreader.Book
import com.isdenmois.readish.shared.ui.noRippleClickable

@Composable
fun LatestBook(book: Book, onClick: () -> Unit) {
    Column(modifier = Modifier.noRippleClickable(onClick = onClick)) {
        Row {
            EbookCover(bitmap = book.cover, size = 124.dp, width = 80.dp)

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(124.dp)
            ) {
                Text(book.title, maxLines = 3, fontSize = 20.sp)

                Spacer(modifier = Modifier.height(4.dp))
                Text(book.author, maxLines = 1, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = book.percent,
                    modifier = Modifier.height(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(11.dp))
        Divider(thickness = 1.dp)
    }
}
