package com.isdenmois.readish.home.presentational.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.isdenmois.readish.home.model.LoadState

@Composable
fun <T> LoadStateContent(state: LoadState<T>, content: @Composable (data: T) -> Unit) {
    when (state) {
        LoadState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            Text(
                state.message,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(16.dp),
            )
        }

        is LoadState.Success -> {
            content(state.data)
        }
    }
}
