package com.isdenmois.readish.transfers.presentational

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.isdenmois.readish.ui.theme.ReadishTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadishTheme {
                TransfersScreen()
            }
        }
    }
}
