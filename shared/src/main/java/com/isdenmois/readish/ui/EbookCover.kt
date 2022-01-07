package com.isdenmois.readish.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

@Composable
fun EbookCover(cover: Bitmap?, ext: String? = null, size: Dp, width: Dp? = null, contentScale: ContentScale = ContentScale.Crop) {
    if (cover != null) {
        var modifier = Modifier.height(size)

        if (width != null) {
            modifier = modifier.width(width)
        }

        Image(
            painter = bitmapPainter(cover),
            contentDescription = "Loading...",
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        FileCover(ext = ext, size = size, width = width)
    }
}

@Composable
fun bitmapPainter(bitmap: Bitmap) = remember(bitmap) {
    BitmapPainter(bitmap.asImageBitmap())
}
