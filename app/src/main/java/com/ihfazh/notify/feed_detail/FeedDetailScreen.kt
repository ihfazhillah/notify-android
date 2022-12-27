package com.ihfazh.notify.feed_detail

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.AnimationVector
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ihfazh.notify.MainActivity
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed_detail.FeedState.*
import com.ihfazh.notify.ui.component.HtmlText
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber
import java.util.regex.Pattern


@OptIn(ExperimentalUnitApi::class, ExperimentalPagerApi::class)
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
    val context = LocalContext.current
    DisposableEffect(key1 = lifecycleOwner){
        val observer = LifecycleEventObserver{ _, event ->
            if (event == Lifecycle.Event.ON_CREATE){
                feedItemViewModel.log(id)
                feedItemViewModel.getFeed(id)

                // additionally: remove from the notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(id)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val feedState = feedItemViewModel.feedState.collectAsState()

    LaunchedEffect(key1 = feedState.value){
        when(feedState.value){
            is Success -> feedItemViewModel.setExample((feedState.value as Success).item.proposalExample?.trim() ?: "")
            else ->
                feedItemViewModel.setExample("")
        }
    }


    val title = when(feedState.value){
        is Empty -> {
            ""
        }
        is Error -> "ERROR"
        is Loading -> "Loading"
        is Success -> {
            (feedState.value as Success).item.title}
    }


    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current

    val example = feedItemViewModel.example.collectAsState()

    val loading = feedItemViewModel.proposalLoading.collectAsState()


    NotifyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)},
                    navigationIcon = {
                        IconButton(onClick = { navigator.navigateUp() }) {
                            Icon(painter = painterResource(id = com.ihfazh.notify.R.drawable.ic_baseline_arrow_back_24), contentDescription = "Go Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val feedItem = (feedState.value as FeedState.Success).item

                            if (feedItem.proposalExample != null){
                                clipboardManager.setText(AnnotatedString(example.value.trim()))
                                Toast.makeText(context, "Proposal Example copied into clipboard", Toast.LENGTH_SHORT).show()
                            }


                            Intent(Intent.ACTION_VIEW, feedItem.guid.toUri()).let{
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                context.startActivity(it)
                            }
                        }) {
                            Icon(painter = painterResource(id = com.ihfazh.notify.R.drawable.ic_baseline_arrow_outward_24), contentDescription = "Open")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (pagerState.currentPage == 1){
                    // in the proposal page
                    androidx.compose.material3.FloatingActionButton(onClick = {
                        feedItemViewModel.loadProposal(id)
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, "Refresh")
                    }
                }
            },
            content = {
                when(feedState.value){
                    is Empty -> {}
                    is Error -> {}
                    is Loading -> {
                        Text("Loading...")
                    }
                    is Success -> {
                        val resp = (feedState.value as Success)
                        Column(
                            Modifier.fillMaxSize()
                        ) {
                            TabRow(
                                // Our selected tab is our current page
                                selectedTabIndex = pagerState.currentPage,
                                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
                                indicator = { tabPositions ->
                                    TabRowDefaults.Indicator(
                                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                                    )
                                },
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                            ) {
                                Tab(
                                    text = { Text("Job Desc") },
                                    selected = pagerState.currentPage == 0,
                                    onClick = {
                                        scope.launch(Dispatchers.Main) {
                                            pagerState.scrollToPage(page = 0)
                                        }
                                    },
                                )
                                Tab(
                                    text = { Text("Proposal Example") },
                                    selected = pagerState.currentPage == 1,
                                    onClick = {
                                        scope.launch(Dispatchers.Main) {
                                            pagerState.scrollToPage(page = 1)
                                        }
                                    },
                                )
                            }

                            HorizontalPager(count = 2, state = pagerState) {
                                if (it == 0){
                                    JobDescriptionContent(feedItem = resp.item)
                                } else {
                                    ProposalExampleContent(example.value, loading=loading.value){ value ->
                                        feedItemViewModel.setExample(value)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        ) 
    }
}

@Composable
@OptIn(ExperimentalUnitApi::class)
private fun ProposalExampleContent(example: String, loading: Boolean = false, onValueChange: (String) -> Unit) {
    val scrollState = rememberScrollState()

    Box(Modifier.fillMaxSize()){
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            if (example.isNotEmpty()) {
//            Text(
//                feedItem.proposalExample.trim(),
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                lineHeight = TextUnit(1.5f, TextUnitType.Em)
//            )
                TextField(
                    value = example,
                    onValueChange = onValueChange,
                    textStyle=androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    colors=TextFieldDefaults.textFieldColors(
                        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                        textColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                )

            }
        }
        if (loading){
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@OptIn(ExperimentalUnitApi::class)
@Composable
fun JobDescriptionContent(feedItem: FeedItemDetail){
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        val type = if (feedItem.budget != null) "Fixed" else "Hourly"
        val price = if (feedItem.budget != null) "$${feedItem.budget}" else "$${feedItem.hourlyRange!![0]}-$${feedItem.hourlyRange[1]}"

        Text(
            feedItem.title,
            fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
            lineHeight = TextUnit(1.5f, TextUnitType.Em)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "$type: $price | ${feedItem.country} | ${feedItem.category} | ${
                feedItem.skills.joinToString(
                    ", "
                )
            }",
            lineHeight = TextUnit(1.5f, TextUnitType.Em)
        )

        Spacer(Modifier.height(28.dp))
        Text(
            feedItem.description,
            lineHeight = TextUnit( 1.5f, TextUnitType.Em ),
            fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize
        )
    }

}

