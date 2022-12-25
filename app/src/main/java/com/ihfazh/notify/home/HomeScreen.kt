package com.ihfazh.notify.home

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.*
import com.ihfazh.notify.NavGraphs
import com.ihfazh.notify.destinations.FeedItemDetailDestination
import com.ihfazh.notify.destinations.LoginScreenDestination
import com.ihfazh.notify.destinations.RequestPermissionScreenDestination
import com.ihfazh.notify.request_permission.RequestPermissionScreen
import com.ihfazh.notify.ui.component.BottomBar
import com.ihfazh.notify.ui.component.FeedListItem
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.utils.navGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandleTiramisuNotificationPermission(viewModel: HomeScreenViewModel){
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class
)
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


    val feeds = homeScreenViewModel.feedItems.collectAsLazyPagingItems()
    var refreshing by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(feeds.loadState.source){
        val isLoading = listOf(
            feeds.loadState.source.refresh is LoadState.Loading,
            feeds.loadState.source.prepend is LoadState.Loading,
            feeds.loadState.source.append is LoadState.Loading,
        ).any { it }
        refreshing = isLoading
    }

    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {feeds.refresh()})
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    val activity = (LocalContext.current as? Activity)

    BackHandler {
        if (listState.firstVisibleItemIndex >= 1) {
            scope.launch {
                listState.scrollToItem(0)
            }
        } else {
            activity?.finish()
        }
    }
    
    val navController = rememberNavController()


    NotifyTheme {
        Scaffold(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            topBar = {
                TopAppBar(contentColor = MaterialTheme.colorScheme.onPrimary, title = {Text("Notify")})
            },
            bottomBar = {
                BottomBar(navController, navigator)
            },
            content = {
//                DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
                NotifyTheme {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(refreshState)){
                        Column (
                        ){
                            Text(
                                text = "Your Feeds",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(16.dp)
                            )

                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                ,
                                state = listState
                            ) {
                                items(feeds.itemCount){ index ->
                                    feeds[index]?.let { feed ->
                                        FeedListItem(item = feed, isRead = feed.accessed, onClick = {
                                            navigator.navigate(FeedItemDetailDestination(feed.id))
                                        })
                                    }
                                }
                            }

                        }

                        PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
                    }

                }
            }
        )
    }
}