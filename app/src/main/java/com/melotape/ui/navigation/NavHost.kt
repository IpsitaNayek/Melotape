package com.melotape.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.melotape.ui.components.MiniPlayerPlaceholder
import com.melotape.ui.screens.auth.SignInScreen
import com.melotape.ui.screens.downloads.DownloadsScreen
import com.melotape.ui.screens.home.HomeRoute
import com.melotape.ui.screens.loved.LovedRoute
import com.melotape.ui.screens.nowplaying.NowPlayingRoute
import com.melotape.ui.screens.profile.ProfileRoute
import com.melotape.ui.screens.search.SearchRoute
import com.melotape.ui.screens.songlist.SongListRoute
import com.melotape.ui.screens.upload.UploadScreen
import com.melotape.ui.theme.*

@Composable
fun MelotapeNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isTopLevelRoute = bottomNavItems.any { it.screen.route == currentDestination?.route }

    val playerViewModel: com.melotape.ui.screens.nowplaying.NowPlayingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.background(Background),
        containerColor = Background,
        bottomBar = {
            if (isTopLevelRoute) {
                Column {
                    // Mini player sits just above the bottom nav
                    com.melotape.ui.components.MiniPlayer(
                        song = playerState.currentSong,
                        isPlaying = playerState.isPlaying,
                        progress = playerState.progress,
                        onPlayPause = playerViewModel::onPlayPause,
                        onToggleLoved = playerViewModel::onToggleLoved,
                        onTap = { navController.navigate(Screen.NowPlaying.route) },
                    )
                    MelotapeBottomBar(
                        navController = navController,
                        currentDest = currentDestination,
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = { fadeIn(tween(220)) },
            exitTransition   = { fadeOut(tween(180)) },
        ) {
            // 1. Home Tab
            composable(Screen.Home.route) {
                HomeRoute(
                    onNavigateToNowPlaying = {
                        navController.navigate(Screen.NowPlaying.route)
                    },
                    onNavigateToSongList = { sourceType, sourceId ->
                        navController.navigate(Screen.SongList.createRoute(sourceType, sourceId))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                )
            }

            // 2. Search Tab
            composable(Screen.Search.route) {
                SearchRoute(
                    onNavigateToNowPlaying = {
                        navController.navigate(Screen.NowPlaying.route)
                    },
                    onNavigateToVault = { vaultTag ->
                        navController.navigate(Screen.SongList.createRoute(vaultTag, ""))
                    },
                )
            }

            // 3. Loved Tab
            composable(Screen.Loved.route) {
                LovedRoute(
                    onNavigateToNowPlaying = {
                        navController.navigate(Screen.NowPlaying.route)
                    },
                    onNavigateToSongList = { sourceType, sourceId ->
                        navController.navigate(Screen.SongList.createRoute(sourceType, sourceId))
                    },
                )
            }

            // 4. Profile Tab
            composable(Screen.Profile.route) {
                ProfileRoute(
                    onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                    onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                    onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                )
            }

            // 5. Now Playing
            composable(
                route = Screen.NowPlaying.route,
                enterTransition = { slideInVertically { it } + fadeIn() },
                exitTransition = { slideOutVertically { it } + fadeOut() },
            ) {
                NowPlayingRoute(
                    onBack = { navController.popBackStack() },
                    viewModel = playerViewModel,
                )
            }

            // 6. Cassette Album Detail / Song List
            composable(
                route = Screen.SongList.route,
                arguments = listOf(
                    navArgument("sourceType") { type = NavType.StringType; defaultValue = "recent" },
                    navArgument("sourceId") { type = NavType.StringType; defaultValue = "" },
                ),
            ) {
                SongListRoute(
                    onBack = { navController.popBackStack() },
                    onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) },
                )
            }

            // 7. Downloads Screen
            composable(Screen.Downloads.route) {
                DownloadsScreen(
                    onBack = { navController.popBackStack() },
                    onSongClick = { navController.navigate(Screen.NowPlaying.route) },
                )
            }

            // 8. Upload Screen
            composable(Screen.Upload.route) {
                UploadScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            // 9. Sign In Screen
            composable(Screen.SignIn.route) {
                SignInScreen(
                    onSignInSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }

            // 10. Debug Component Gallery
            composable(Screen.Gallery.route) {
                com.melotape.ui.gallery.GalleryScreen()
            }
        }
    }
}

@Composable
private fun MelotapeBottomBar(
    navController: NavHostController,
    currentDest: androidx.navigation.NavDestination?,
) {
    NavigationBar(
        containerColor = Surface,
        contentColor = TextSecondary,
        tonalElevation = 0.dp,
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDest?.hierarchy?.any { it.route == item.screen.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryAccent,
                    selectedTextColor = PrimaryAccent,
                    indicatorColor = SurfaceElevated,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                ),
            )
        }
    }
}
