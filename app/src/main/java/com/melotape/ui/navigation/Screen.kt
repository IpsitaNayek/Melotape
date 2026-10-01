package com.melotape.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.melotape.R

sealed class Screen(val route: String) {
    // Primary tabs
    data object Home          : Screen("home")
    data object Search        : Screen("search")
    data object Loved         : Screen("loved")
    data object Profile       : Screen("profile")

    // Secondary screens
    data object NowPlaying    : Screen("now_playing")
    data object SongList      : Screen("song_list/{sourceType}/{sourceId}") {
        fun createRoute(sourceType: String, sourceId: String = "") =
            "song_list/$sourceType/$sourceId"
    }
    data object Downloads     : Screen("downloads")
    data object SignIn        : Screen("sign_in")
    data object SignUp        : Screen("sign_up")
    data object ForgotPass    : Screen("forgot_password")
    data object Upload        : Screen("upload")
    data object CreatePlaylist: Screen("create_playlist")
    data object AudioQuality  : Screen("audio_quality")
    data object AnalogEq      : Screen("analog_eq")
    data object About         : Screen("about")
    data object Gallery       : Screen("gallery")  // debug-only

    companion object {
        val mainTabs = listOf(Home, Search, Loved, Profile)
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,    "Home",    Icons.Filled.Home,     Icons.Outlined.Home),
    BottomNavItem(Screen.Search,  "Search",  Icons.Filled.Search,   Icons.Outlined.Search),
    BottomNavItem(Screen.Loved,   "Loved",   Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person,   Icons.Outlined.Person),
)
