package com.ihfazh.notify.feed_detail

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed_detail.FeedState.*
import com.ihfazh.notify.ui.component.HtmlText
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalUnitApi::class)
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
        is Empty -> {
            ""
        }
        is Error -> "ERROR"
        is Loading -> "Loading"
        is Success -> {
            (feedState.value as Success).item.title}
    }

    val scrollState = rememberScrollState()


    NotifyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)},
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
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                ) {
                    when(feedState.value){
                        is Empty -> {}
                        is Error -> {}
                        is Loading -> {
                            Text("Loading...")
                        }
                        is Success -> {
                            val resp = (feedState.value as Success)

                            val type = if (resp.item.budget != null) "Fixed" else "Hourly"
                            val price = if (resp.item.budget != null) "$${resp.item.budget}" else "$${resp.item.hourlyRange!![0]}-$${resp.item.hourlyRange[1]}"

                            val item = resp.item

                            Text(
                                resp.item.title,
                                fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "$type: $price | ${item.country} | ${item.category} | ${
                                    item.skills.joinToString(
                                        ","
                                    )
                                }",
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )

                            Spacer(Modifier.height(28.dp))
                            Text(
                                resp.item.description,
                                lineHeight = TextUnit( 1.5f, TextUnitType.Em ),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize
                            )

                            if (item.proposalExample != null){
                                Spacer(Modifier.height(60.dp))
                                Text(
                                    "Proposal Example",
                                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
                                    lineHeight = TextUnit(1.5f, TextUnitType.Em)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    item.proposalExample,
                                    fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                    lineHeight = TextUnit(1.5f, TextUnitType.Em)
                                )

                            }
                        }
                    }
                }
            }
        ) 
    }
}
