package com.ihfazh.notify

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.rememberNavController
import com.ihfazh.notify.destinations.HomeScreenDestination
import com.ihfazh.notify.destinations.PromptScreenDestination
import com.ihfazh.notify.service.MarkAsReadBroadcastReceiver
import com.ihfazh.notify.ui.component.BottomBar
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

class MainActivity : ComponentActivity() {
    private val markAsReadReceiver = MarkAsReadBroadcastReceiver()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(markAsReadReceiver, IntentFilter("mark-as-read"))
        setContent {
            NotifyApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(markAsReadReceiver)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifyApp(
){
    val navController = rememberNavController()
    val destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startRoute

    NotifyTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(
            bottomBar = {
                if (destination in listOf(
                        HomeScreenDestination,
                        PromptScreenDestination
                    )){
                    BottomBar(navController = navController)
                }
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
            }
        }
    }
}