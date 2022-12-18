package com.ihfazh.notify.home

import androidx.compose.material.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ihfazh.notify.destinations.LoginScreenDestination
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start=true)
@Destination()
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeScreenViewModel: HomeScreenViewModel = getViewModel()
){
    LaunchedEffect("hasToken" ){
        val hasToken = homeScreenViewModel.getToken() != null

        if (!hasToken){
            navigator.navigate(LoginScreenDestination)
        }
    }

    NotifyTheme {
        Scaffold(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            topBar = {
                TopAppBar(contentColor = MaterialTheme.colorScheme.onPrimary) {
                    Text("Notify")
                }
            },
            content = {
                Text("Hello world")
            }
        )
    }
}