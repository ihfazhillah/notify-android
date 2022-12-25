package com.ihfazh.notify.ui.component

import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.ihfazh.notify.NavGraphs
import com.ihfazh.notify.appCurrentDestinationAsState
import com.ihfazh.notify.destinations.Destination
import com.ihfazh.notify.destinations.HomeScreenDestination
import com.ihfazh.notify.destinations.PromptScreenDestination
import com.ihfazh.notify.startAppDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.navigateTo
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
) {
    Home(HomeScreenDestination, Icons.Default.Home, "Home"),
    Prompt(PromptScreenDestination, Icons.Default.Add, "Prompt")
}

@Composable
fun BottomBar(
    navController: NavController,
    navigator: DestinationsNavigator
){
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    BottomNavigation {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navigator.navigate(destination.direction)
                },
                icon = { Icon(destination.icon, contentDescription = destination.label)},
                label = { Text(destination.label) },
            )

        }
    }
}