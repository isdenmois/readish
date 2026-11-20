package com.isdenmois.readish.transfers.presentational

import androidx.compose.ui.graphics.ImageBitmap

data class TransfersUiState(val address: String = "", val qr: ImageBitmap? = null)
