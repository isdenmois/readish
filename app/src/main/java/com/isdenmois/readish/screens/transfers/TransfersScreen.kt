package com.isdenmois.readish.screens.transfers

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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TransfersScreen() {
    val vm: TransfersViewModel = viewModel()
    val address by vm.addressState
    val qr by vm.qrState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        if (qr != null) {
            Image(bitmap = qr!!, contentDescription = address, modifier = Modifier.size(512.dp))
        }

        Text(address, fontSize = 32.sp)

         if (address.isBlank()) {
             Text("WiFi is turning on")
         }
    }
}
