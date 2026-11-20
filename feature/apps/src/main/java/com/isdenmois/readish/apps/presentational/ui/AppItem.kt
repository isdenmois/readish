package com.isdenmois.readish.apps.presentational.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isdenmois.readish.apps.model.AppEntry
import com.isdenmois.readish.ui.bitmapPainter
import com.isdenmois.readish.ui.noRippleClickable
import com.isdenmois.readish.ui.R as SharedR

@Composable
fun AppItem(app: AppEntry, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .noRippleClickable(onClick)
            .padding(8.dp),
    ) {
        if (app.icon != null) {
            Image(
                painter = bitmapPainter(app.icon),
                contentDescription = app.label,
                modifier = Modifier.size(ICON_SIZE),
                contentScale = ContentScale.Fit,
            )
        } else {
            Image(
                painter = painterResource(SharedR.drawable.ic_apps),
                contentDescription = app.label,
                modifier = Modifier
                    .size(ICON_SIZE)
                    .padding(PLACEHOLDER_PADDING),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = app.label,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val ICON_SIZE = 64.dp
private val PLACEHOLDER_PADDING = 16.dp
