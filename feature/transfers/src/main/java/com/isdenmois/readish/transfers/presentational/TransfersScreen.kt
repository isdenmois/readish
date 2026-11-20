package com.isdenmois.readish.transfers.presentational

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isdenmois.readish.transfers.R

@Composable
fun TransfersScreen(onBack: () -> Unit, vm: TransfersViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    TransfersContent(
        state = state,
        onBack = onBack,
        onOpenWifiSettings = vm::openWifiSettings,
    )
}

@Composable
fun TransfersContent(state: TransfersUiState, onBack: () -> Unit, onOpenWifiSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Text(stringResource(R.string.back))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            state.qr?.let { qr ->
                Image(
                    bitmap = qr,
                    contentDescription = state.address,
                    modifier = Modifier.size(512.dp),
                )
            }

            Text(state.address, style = MaterialTheme.typography.headlineMedium)

            if (state.address.isBlank()) {
                Text(
                    text = stringResource(R.string.wifi_off),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                TextButton(onClick = onOpenWifiSettings) {
                    Text(stringResource(R.string.open_wifi_settings))
                }
            }
        }
    }
}
