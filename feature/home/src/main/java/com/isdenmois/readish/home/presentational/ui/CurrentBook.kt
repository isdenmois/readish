package com.isdenmois.readish.home.presentational.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isdenmois.readish.home.model.Book
import com.isdenmois.readish.ui.EbookCover
import com.isdenmois.readish.ui.noRippleClickable

@Composable
fun CurrentBook(book: Book, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.noRippleClickable(onClick = onClick),
    ) {
        Box {
            EbookCover(cover = book.cover, ext = book.file.extension, size = 360.dp)

            Text(
                "${book.page} / ${book.pages}",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(0.75f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            LinearProgressIndicator(
                progress = book.percent,
                modifier = Modifier
                    .height(8.dp)
            )
        }
    }
}
