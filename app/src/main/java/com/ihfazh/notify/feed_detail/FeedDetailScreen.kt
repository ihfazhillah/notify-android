package com.ihfazh.notify.feed_detail

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.AnimationVector
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
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
import com.ihfazh.notify.R
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed_detail.FeedState.*
import com.ihfazh.notify.proposal.MyProposalState
import com.ihfazh.notify.ui.component.HtmlText
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber
import java.util.regex.Pattern


@OptIn(ExperimentalUnitApi::class, ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
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

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded}
    )


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
                androidx.compose.material3.FloatingActionButton(onClick = {
                    feedItemViewModel.loadMyProposal(id)
                    scope.launch {
                        bottomSheetState.show()
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_edit_note_24), "Refresh")
                }
            }
        ) {
            when (feedState.value) {
                is Empty -> {}
                is Error -> {}
                is Loading -> {
                    Text("Loading...")
                }
                is Success -> {
                    ModalBottomSheetLayout(
                        sheetState = bottomSheetState,
                        sheetContent = {
                            ProposalWriter(
                                viewModel = feedItemViewModel,
                                sheetState = bottomSheetState,
                                feedId = id
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val resp = (feedState.value as Success)
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(it)
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
                                    text = { Text("Tools") },
                                    selected = pagerState.currentPage == 1,
                                    onClick = {
                                        scope.launch(Dispatchers.Main) {
                                            pagerState.scrollToPage(page = 1)
                                        }
                                    },
                                )
                            }

                            HorizontalPager(count = 2, state = pagerState) {
                                if (it == 0) {
                                    JobDescriptionContent(feedItem = resp.item)
                                } else {
                                    FeedItemTools(
                                        feedItem = resp.item,
                                        viewModel = feedItemViewModel
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalUnitApi::class)
@Composable
fun ExpandableItem(
    modifier: Modifier = Modifier,
    title: String,
    onToggle: (Boolean) -> Unit = {},
    actions: @Composable () -> Unit = {},
    content: @Composable () -> Unit
){
    var isOpen by remember{
        mutableStateOf(false)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    1.dp,
                    androidx.compose.material3.MaterialTheme.colorScheme.primary
                ), RoundedCornerShape(10.dp)
            )
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    isOpen = !isOpen
                    onToggle.invoke(isOpen)
                }
                .padding(16.dp)
                    ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
                lineHeight = TextUnit(1.5f, TextUnitType.Em)
            )

            Icon(
                imageVector = if (isOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowRight,
                contentDescription = "toggle"
            )

        }

        if (isOpen){
            Divider(thickness = 1.dp, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)

            Column (
                Modifier.padding(16.dp)
            ){
                content()
            }

            Divider(
                thickness = 1.dp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp, 0.dp)
            )

            actions()
        }


    }

}


@OptIn(ExperimentalUnitApi::class)
@Composable
private fun FeedItemTools(
    feedItem: FeedItemDetail,
    viewModel: FeedDetailViewModel
){
    val scrollState = rememberScrollState()

    val summaryLoading = viewModel.summarizeLoading.collectAsState()
    val summaryString = viewModel.summarizeString.collectAsState()

    val teaserLoading = viewModel.teaserLoading.collectAsState()
    val teaserString = viewModel.teaserString.collectAsState()

    val questionLoading = viewModel.questionLoading.collectAsState()
    val questionString = viewModel.questionString.collectAsState()

    val suggestionLoading = viewModel.suggestionLoading.collectAsState()
    val suggestionString = viewModel.suggestionString.collectAsState()

    val keyPointsLoading = viewModel.keyPointsLoading.collectAsState()
    val keyPointsString = viewModel.keyPointsString.collectAsState()

    val proposalLoading = viewModel.proposalLoading.collectAsState()
    val proposalString = viewModel.example.collectAsState()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){

        item{
            ExpandableItem(
                title = "Summarize",
                onToggle = {
                    if (it && summaryString.value.isEmpty()) {
                        viewModel.getSummarize("Job Description: [${feedItem.description}]")
                    }
                },
                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = {
                            viewModel.getSummarize("Job Description: [${feedItem.description}]")
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }

                }
            ){
                Box(Modifier.fillMaxSize()){

                    if (summaryLoading.value){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        SelectionContainer {
                            Text(
                                summaryString.value.trim(),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                fontStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontStyle,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )
                        }
                    }

                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
        }


        item{
            ExpandableItem(
                title = "Teaser",
                onToggle = {
                   if (it && teaserString.value.isEmpty()){
                       viewModel.getTeaser("Job Description: [${feedItem.description}]")
                   }
                },
                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = {
                            viewModel.getTeaser("Job Description: [${feedItem.description}]")
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }

                }
            ){
                Box(Modifier.fillMaxSize()){

                    if (teaserLoading.value){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        SelectionContainer {
                            Text(
                                teaserString.value.trim(),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                fontStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontStyle,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )
                        }
                    }

                }
            }
        }


        item {
            Spacer(Modifier.height(16.dp))
        }


        item{
            ExpandableItem(
                title = "Suggestion",
                onToggle = {
                    if (it && suggestionString.value.isEmpty()){
                        viewModel.getSuggestion("Job Description: [${feedItem.description}]")
                    }
                },
                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = {
                            viewModel.getSuggestion("Job Description: [${feedItem.description}]")
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            ){
                Box(Modifier.fillMaxSize()){

                    if (suggestionLoading.value){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        SelectionContainer {
                            Text(
                                suggestionString.value.trim(),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                fontStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontStyle,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em),
                            )
                        }
                    }

                }
            }
        }


        item {
            Spacer(Modifier.height(16.dp))
        }


        item{
            ExpandableItem(
                title = "Key Points",
                onToggle = {
                    if (it && keyPointsString.value.isEmpty()){
                        viewModel.getKeyPoints("Job Description: [${feedItem.description}]")
                    }
                },

                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = {
                            viewModel.getKeyPoints("Job Description: [${feedItem.description}]")
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            ){
                Box(Modifier.fillMaxSize()){

                    if (keyPointsLoading.value){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        SelectionContainer {
                            Text(
                                keyPointsString.value.trim(),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                fontStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontStyle,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )
                        }
                    }

                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
        }


        item{
            ExpandableItem(
                title = "Questions",
                onToggle = {
                    if (it && questionString.value.isEmpty()){
                        viewModel.getQuestion("Job Description: [${feedItem.description}]")
                    }
                },

                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = {
                            viewModel.getQuestion("Job Description: [${feedItem.description}]")
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            ){
                Box(Modifier.fillMaxSize()){

                    if (questionLoading.value){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        SelectionContainer {
                            Text(
                                questionString.value.trim(),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                fontStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontStyle,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )
                        }
                    }

                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
        }


        item{
            ExpandableItem(
                title = "Proposal",
                onToggle = {
                    if (it && proposalString.value.isEmpty()){
                        viewModel.loadProposal(feedItem.id)
                    }
                },
                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        IconButton(onClick = {
                            viewModel.loadProposal(feedItem.id)
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            ){
                Box(Modifier.fillMaxSize()){

                    if (proposalLoading.value){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    } else {
                        SelectionContainer {
                            Text(
                                proposalString.value.trim(),
                                fontSize = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontSize,
                                fontStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.fontStyle,
                                lineHeight = TextUnit(1.5f, TextUnitType.Em)
                            )
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProposalWriter(
    viewModel: FeedDetailViewModel,
    sheetState: ModalBottomSheetState,
    feedId: Int
){

    val scrollState = rememberScrollState()

    val myProposalState: State<MyProposalState> = viewModel.myProposalState.collectAsState()
    val myProposalString : State<String> = viewModel.myProposalString.collectAsState()

    LaunchedEffect(myProposalString.value){
        if (myProposalString.value.isNotEmpty()){
            delay(3000)
            viewModel.saveMyProposal(feedId)
        }
    }

    val statusString : State<String> = remember{
        derivedStateOf {
            when(myProposalState.value){
                MyProposalState.Initial -> "Initial"
                MyProposalState.Draft -> "Draft"
                is MyProposalState.Error -> (myProposalState.value as MyProposalState.Error).message ?: "Unknown"
                MyProposalState.Loading -> "Loading..."
                MyProposalState.Saved -> "Saved..."
                MyProposalState.Saving -> "Saving..."
            }
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ){

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column{
                Text(
                    "My Proposal",
                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
                    fontStyle = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontStyle,
                )
                Text(
                    statusString.value,
                    fontSize = androidx.compose.material3.MaterialTheme.typography.labelLarge.fontSize,
                    fontStyle = androidx.compose.material3.MaterialTheme.typography.labelLarge.fontStyle,
                )
            }

            IconButton(onClick = {
                clipboardManager.setText(
                    AnnotatedString(myProposalString.value)
                )
                Toast.makeText(context, "Proposal Copied", Toast.LENGTH_SHORT).show()
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_content_copy_24), contentDescription = "Copy")
            }
        }

        Divider()
        Spacer(Modifier.height(8.dp))

        androidx.compose.material3.OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            value = myProposalString.value,
            onValueChange = {
                viewModel.setMyProposal(it)
            },
            singleLine = false,
            minLines = 10
        )


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

