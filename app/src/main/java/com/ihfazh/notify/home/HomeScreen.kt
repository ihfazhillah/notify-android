package com.ihfazh.notify.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.*
import com.ihfazh.notify.destinations.LoginScreenDestination
import com.ihfazh.notify.destinations.RequestPermissionScreenDestination
import com.ihfazh.notify.request_permission.RequestPermissionScreen
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.getViewModel
import timber.log.Timber


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandleTiramisuNotificationPermission(viewModel: HomeScreenViewModel){
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@RootNavGraph(start=true)
@Destination()
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeScreenViewModel: HomeScreenViewModel = getViewModel(),
    resultRecipient: ResultRecipient<LoginScreenDestination, Boolean>
){

//    todo: handle if we need this currently, my phone is on the version 12.
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//        HandleTiramisuNotificationPermission(homeScreenViewModel)
//    }
//    val postNotificationState = rememberPermissionState( Manifest.permission.POST_NOTIFICATIONS )
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(key1 = lifecycleOwner){
//        Timber.d("Hello world disposable effect")
//        val observer = LifecycleEventObserver{ _, event ->
//            if (event == Lifecycle.Event.ON_RESUME){
//                Timber.d("Hello world from on resume event")
//                postNotificationState.launchPermissionRequest()
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
//

    LaunchedEffect(homeScreenViewModel.getToken()){
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
                NotifyTheme {
                    Column {
                        Text("Hello world")
                    }

                }
            }
        )
    }
}