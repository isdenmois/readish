package com.isdenmois.readish.home.presentational.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.ui.EbookCover
import com.isdenmois.readish.ui.noRippleClickable

@Composable
fun BookFileItem(book: EBookFile, onClick: () -> Unit) {
    Column(modifier = Modifier.noRippleClickable(onClick)) {
        EbookCover(
            cover = book.cover,
            ext = book.file.extension,
            size = 150.dp,
            width = 100.dp,
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            book.title,
            maxLines = 1,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp)
        )
    }
}
