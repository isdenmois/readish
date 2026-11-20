package com.isdenmois.readish.library.presentational.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isdenmois.readish.library.model.LibraryBook
import com.isdenmois.readish.ui.EbookCover
import com.isdenmois.readish.ui.noRippleClickable

@Composable
fun LibraryBookItem(book: LibraryBook, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .noRippleClickable(onClick)
            .padding(4.dp),
    ) {
        EbookCover(
            cover = book.cover,
            ext = book.file.extension,
            size = 150.dp,
            width = 110.dp,
            contentScale = ContentScale.FillWidth,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = book.title,
            maxLines = 2,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = book.subtitle,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
