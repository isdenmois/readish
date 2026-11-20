package com.isdenmois.readish

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.isdenmois.readish.home.presentational.HomeScreen
import com.isdenmois.readish.transfers.presentational.TransfersScreen
import com.isdenmois.readish.ui.theme.ReadishTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object TransfersRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var homeIntent by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    val currentHomeIntent = homeIntent

                    LaunchedEffect(currentHomeIntent) {
                        if (currentHomeIntent > 0) {
                            navController.popBackStack<HomeRoute>(inclusive = false)
                        }
                    }

                    NavHost(navController = navController, startDestination = HomeRoute) {
                        composable<HomeRoute> {
                            HomeScreen(
                                onOpenTransfers = {
                                    navController.navigate(TransfersRoute) { launchSingleTop = true }
                                },
                            )
                        }
                        composable<TransfersRoute> {
                            TransfersScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == Intent.ACTION_MAIN) {
            homeIntent++
        }
    }
}
