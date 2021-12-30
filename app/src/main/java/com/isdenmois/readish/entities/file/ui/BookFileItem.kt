package com.isdenmois.readish.entities.file.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isdenmois.ebookparser.EBookFile
import com.isdenmois.readish.R
import com.isdenmois.readish.shared.ui.noRippleClickable

@Composable
fun BookFileItem(file: EBookFile, onClick: () -> Unit) {
    val loadingPainter = painterResource(R.drawable.ic_loading)
    val painter = remember(file.cover) {
        if (file.cover !== null) {
            BitmapPainter(file.cover!!.asImageBitmap())
        } else {
            loadingPainter
        }
    }

    Column(modifier = Modifier.noRippleClickable(onClick)) {
        Image(
            painter = painter,
            contentDescription = "Loading...",
            modifier = Modifier
                .width(100.dp)
                .height(150.dp),
            contentScale = ContentScale.FillWidth,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            file.title,
            maxLines = 1,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp)
        )
    }
}
