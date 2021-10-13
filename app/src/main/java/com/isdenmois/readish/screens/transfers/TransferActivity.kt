package com.isdenmois.readish.screens.transfers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.isdenmois.readish.shared.ui.theme.ReadishTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferActivity : ComponentActivity() {
    private val vm by viewModels<TransfersViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadishTheme {
                TransfersScreen()
            }
        }
    }
}
