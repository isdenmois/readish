package com.isdenmois.readish.shared.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.isdenmois.readish.R

@Composable
fun EbookCover(bitmap: Bitmap?, size: Dp, width: Dp? = null) {
    var modifier = Modifier.height(size)

    if (width != null) {
        modifier = modifier.width(width)
    }

    Image(
        painter = thumbnailPainter(bitmap),
        contentDescription = "Loading...",
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun thumbnailPainter(bitmap: Bitmap?): Painter {
    val loadingPainter = painterResource(R.drawable.ic_loading)
    val painter = remember(bitmap) {
        if (bitmap != null) {
            BitmapPainter(bitmap.asImageBitmap())
        } else {
            null
        }
    }

    if (painter != null) {
        return painter
    }

    return loadingPainter
}
