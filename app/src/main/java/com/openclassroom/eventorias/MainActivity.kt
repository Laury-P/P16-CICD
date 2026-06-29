package com.openclassroom.eventorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AuthScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EventListScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination

import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.contains

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventoriasTheme {
                val navController = rememberNavController()

                val currentDestination = navController.currentDestinationAsState().value
                    ?: NavGraphs.root.startDestination

                val destinationsWithNavBar = listOf(
                    EventListScreenDestination,
                    ProfileScreenDestination
                )

                Scaffold(
                    modifier = Modifier,
                    bottomBar = {
                        if (currentDestination in destinationsWithNavBar)
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background,
                            ) {
                                // Onglet Event List
                                NavigationBarItem(
                                    selected = currentDestination == EventListScreenDestination,
                                    onClick = { navController.navigate(EventListScreenDestination.route) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Event,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(R.string.event_nav_button)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                                        indicatorColor = MaterialTheme.colorScheme.surface
                                    )
                                )

                                // Onglet Profile
                                NavigationBarItem(
                                    selected = currentDestination == ProfileScreenDestination,
                                    onClick = { navController.navigate(ProfileScreenDestination.route) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.PersonOutline,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(R.string.profile_nav_button)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                                        indicatorColor = MaterialTheme.colorScheme.surface

                                    )
                                )
                            }
                    }

                ) { innerPadding ->
                    val isAuthScreen = currentDestination == AuthScreenDestination
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        navController = navController,
                        modifier = if (isAuthScreen) Modifier else Modifier.padding (innerPadding)
                    )
                }
            }
        }
    }
}

