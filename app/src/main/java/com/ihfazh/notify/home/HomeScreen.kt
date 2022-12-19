package com.ihfazh.notify.home

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*
import com.ihfazh.notify.destinations.LoginScreenDestination
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@RootNavGraph(start=true)
@Destination()
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeScreenViewModel: HomeScreenViewModel = getViewModel(),
    resultRecipient: ResultRecipient<LoginScreenDestination, Boolean>
){

    LaunchedEffect("hasToken", ){
        val hasToken = homeScreenViewModel.getToken() != null

        if (!hasToken){
            navigator.navigate(LoginScreenDestination)
        }
    }

    val postNotificationState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
    }  else {
        homeScreenViewModel.setNotificationPermissionGranted(true)
        null
    }

    val permissionGranted = homeScreenViewModel.notificationPermissionGranted.collectAsState()

    if (permissionGranted.value){
       LaunchedEffect("permissionGranted") {
           homeScreenViewModel.registerDevice()
       }
    }

    val context = LocalContext.current

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                if (result.value){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val postNotificationState = postNotificationState!!
                        if (postNotificationState.status.isGranted) {
                            homeScreenViewModel.setNotificationPermissionGranted(true)
                        } else {
                            val textToShow = if (postNotificationState.status.shouldShowRationale) {
                                "The new data will landed into your notification screen if you enable this."
                            } else {
                                "Notification permission not granted. We will not send notification for the new data"
                            }
                            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
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
                NotifyTheme {
                    Column {
                        Text("Hello world")
                    }

                }
            }
        )
    }
}