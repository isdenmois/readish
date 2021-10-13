package com.isdenmois.readish.entities.book.ui

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
import com.isdenmois.readish.shared.ui.EbookCover
import com.isdenmois.readish.shared.api.alreader.Book
import com.isdenmois.readish.shared.ui.noRippleClickable

@Composable
fun CurrentBook(book: Book, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.noRippleClickable(onClick = onClick),
    ) {
        Box {
            EbookCover(bitmap = book.cover, size = 360.dp)
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
