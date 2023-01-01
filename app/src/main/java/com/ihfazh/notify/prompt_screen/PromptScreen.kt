package com.ihfazh.notify.prompt_screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ihfazh.notify.NavGraphs
import com.ihfazh.notify.destinations.HomeScreenDestination
import com.ihfazh.notify.destinations.PromptFormScreenDestination
import com.ihfazh.notify.ui.component.BottomBar
import com.ihfazh.notify.ui.component.PromptListItem
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.utils.navGraph
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Destination()
@Composable
fun PromptScreen(
    navigator: DestinationsNavigator,
    promptViewModel: PromptScreenViewModel = getViewModel()
) {

    val prompts = promptViewModel.prompts.collectAsLazyPagingItems()
    var refreshing  by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(prompts.loadState.source){
        val isLoading = listOf(
            prompts.loadState.source.refresh is LoadState.Loading,
//            prompts.loadState.source.prepend is LoadState.Loading,
//            prompts.loadState.source.append is LoadState.Loading,
        ).any { it }
        refreshing = isLoading
    }

    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {prompts.refresh()})
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()


    BackHandler {
        if (listState.firstVisibleItemIndex >= 1) {
            scope.launch {
                listState.scrollToItem(0)
            }
        } else {
            navigator.navigate(HomeScreenDestination)
        }
    }

    val navController = rememberNavController()

    Scaffold(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        topBar = {
            TopAppBar(contentColor = MaterialTheme.colorScheme.onPrimary, title = { Text("Prompts") })
        },
        bottomBar = {
                    BottomAppBar() {
                        // just to trick the floating action button
                    }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    navigator.navigate(PromptFormScreenDestination.route)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = {
            NotifyTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(it)
                ) {
                    Column {
                        Text(
                            text = "Prompts",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn(
                            Modifier.fillMaxSize(),
                            state = listState
                        ){

                            items(prompts.itemCount){ index ->
                                prompts[index]?.let { prompt ->
                                    PromptListItem(
                                        prompt = prompt,
                                        onItemActivate = {
                                            promptViewModel.select(prompt.id)
                                            prompts.refresh()
                                        },
                                        onItemEdit = {
                                            navigator.navigate(PromptFormScreenDestination(it))
                                        }
                                    )
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