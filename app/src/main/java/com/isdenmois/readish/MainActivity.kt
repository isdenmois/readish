package com.isdenmois.readish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.isdenmois.readish.home.presentational.HomeScreen
import com.isdenmois.readish.home.presentational.HomeViewModel
import com.isdenmois.readish.ui.theme.ReadishTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

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
