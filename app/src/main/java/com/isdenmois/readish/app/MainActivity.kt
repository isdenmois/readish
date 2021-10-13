package com.isdenmois.readish.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.isdenmois.readish.screens.home.HomeScreen
import com.isdenmois.readish.screens.home.HomeViewModel
import com.isdenmois.readish.shared.ui.theme.ReadishTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        homeViewModel.loadBooks()
    }
}
