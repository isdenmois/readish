package com.isdenmois.readish.shared.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.isdenmois.readish.R
import com.isdenmois.readish.shared.api.parser.BookParser
import kotlinx.coroutines.launch

@Composable
fun EbookCover(path: String, size: Dp, width: Dp? = null) {
    var modifier = Modifier.height(size)

    if (width != null) {
        modifier = modifier.width(width)
    }

    Image(
        painter = thumbnailPainter(path),
        contentDescription = "Loading...",
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

val parsed = hashMapOf<String, Bitmap>()

@Composable
fun thumbnailPainter(path: String): Painter {
    var bitmap by remember { mutableStateOf(parsed[path]) }
    val coroutineScope = rememberCoroutineScope()
    val loadingPainter = painterResource(R.drawable.ic_loading)
    val painter = remember(bitmap) {
        if (bitmap != null) {
            BitmapPainter(bitmap!!.asImageBitmap())
        } else {
            loadingPainter
        }
    }

    LaunchedEffect(path) {
        bitmap = parsed[path]

        if (bitmap == null) {
            coroutineScope.launch {
                val parser = BookParser.getParser(path)

                bitmap = parser.parse()?.cover
                bitmap?.let {
                    parsed[path] = it
                }
            }
        }

    }

    return painter
}
