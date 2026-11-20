package com.isdenmois.readish.transfers.presentational

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransfersScreen(vm: TransfersViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val address = state.address
    val qr = state.qr

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        if (qr != null) {
            Image(bitmap = qr, contentDescription = address, modifier = Modifier.size(512.dp))
        }

        Text(address, fontSize = 32.sp)

        if (address.isBlank()) {
            Text("WiFi is turning on")
        }
    }
}
