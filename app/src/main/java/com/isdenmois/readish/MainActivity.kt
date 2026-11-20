package com.isdenmois.readish

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.isdenmois.readish.apps.presentational.AppsScreen
import com.isdenmois.readish.home.presentational.HomeScreen
import com.isdenmois.readish.library.presentational.LibraryScreen
import com.isdenmois.readish.transfers.presentational.TransfersScreen
import com.isdenmois.readish.ui.theme.ReadishTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object TransfersRoute

@Serializable
data object LibraryRoute

@Serializable
data object AppsRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var homeIntent by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReadishTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val currentHomeIntent = homeIntent

                    LaunchedEffect(currentHomeIntent) {
                        if (currentHomeIntent > 0) {
                            navController.popBackStack<HomeRoute>(inclusive = false)
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(
                                onOpenTransfers = {
                                    navController.navigate(TransfersRoute) { launchSingleTop = true }
                                },
                                onOpenLibrary = {
                                    navController.navigate(LibraryRoute) { launchSingleTop = true }
                                },
                                onOpenApps = {
                                    navController.navigate(AppsRoute) { launchSingleTop = true }
                                },
                            )
                        }
                        composable<TransfersRoute> {
                            TransfersScreen(onBack = { navController.popBackStack() })
                        }
                        composable<LibraryRoute> {
                            LibraryScreen(onBack = { navController.popBackStack() })
                        }
                        composable<AppsRoute> {
                            AppsScreen(onBack = { navController.popBackStack() })
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
