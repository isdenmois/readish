package com.isdenmois.readish.home.presentational.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.ui.EbookCover
import com.isdenmois.readish.ui.noRippleClickable

@Composable
fun LatestBook(book: Book, onClick: () -> Unit) {
    Column(modifier = Modifier.noRippleClickable(onClick = onClick)) {
        Row {
            EbookCover(cover = book.cover, ext = book.file.extension, size = 124.dp, width = 80.dp)

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(124.dp),
            ) {
                Text(
                    book.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    book.author,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { book.percent },
                    modifier = Modifier.height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    drawStopIndicator = {},
                )
            }
        }

        Spacer(modifier = Modifier.height(11.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
    }
}
