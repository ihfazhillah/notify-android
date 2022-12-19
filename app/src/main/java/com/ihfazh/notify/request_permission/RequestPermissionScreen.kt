package com.ihfazh.notify.request_permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Destination()
@Composable
fun RequestPermissionScreen(
    navigator: ResultBackNavigator<Boolean>
){
    val notificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS){
        navigator.navigateBack(result=it)
    }

    NotifyTheme {
        Scaffold {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Give us notification access in order to receive notification")
                Button(onClick = { navigator.navigateBack(result=false) }) {
                    Text("SKIP")
                }
                Button(onClick = {
                    notificationPermission.launchPermissionRequest()
                }) {
                    Text("Accept")
                }
            }

        }
    }
}