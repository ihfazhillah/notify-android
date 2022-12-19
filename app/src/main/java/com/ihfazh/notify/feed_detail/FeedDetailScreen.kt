package com.ihfazh.notify.feed_detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ihfazh.notify.feed_detail.FeedState.*
import com.ihfazh.notify.ui.component.HtmlText
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination(
    route="item_detail",
    deepLinks = [
        DeepLink(uriPattern = "https://notify.ihfazh.com/deeplink/$FULL_ROUTE_PLACEHOLDER")
    ]
)
@Composable
fun FeedItemDetail(
    id: Int,
    feedItemViewModel: FeedDetailViewModel = getViewModel(),
    navigator: DestinationsNavigator
){
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner){
        val observer = LifecycleEventObserver{ _, event ->
            if (event == Lifecycle.Event.ON_CREATE){
                feedItemViewModel.log(id)
                feedItemViewModel.getFeed(id)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val feedState = feedItemViewModel.feedState.collectAsState()

    val title = when(feedState.value){
        Empty -> {
            ""
        }
        is Error -> "ERROR"
        Loading -> "Loading"
        is Success -> {
            (feedState.value as Success).item.title}
    }


    NotifyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text(title.take(25))},
                    navigationIcon = {
                        IconButton(onClick = { navigator.navigateUp() }) {
                            Icon(painter = painterResource(id = com.ihfazh.notify.R.drawable.ic_baseline_notifications_24), contentDescription = "Go Back")
                        }
                    }
                )
            },
            content = {
                Column(Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                ) {
                    if (feedState.value is FeedState.Success){
                        HtmlText(html = (feedState.value as Success).item.content)
                    }
                }
            }
        ) 
    }
}
