package github.antaif.urfuandroidstories.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

data object Routes {
    data object MainScreen : TopLevelRoute {
        override val icon = Icons.Default.Home
    }

    data class StoriesViewingScreen(val startIndex: Int) : Route
}