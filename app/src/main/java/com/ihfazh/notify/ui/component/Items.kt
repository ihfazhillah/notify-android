package com.ihfazh.notify.ui.component

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.ihfazh.notify.feed.SimpleFeedItem
import com.ihfazh.notify.prompt.ProposalPrompt
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun FeedListItem(
    item: SimpleFeedItem,
    isRead: Boolean = false,
    onClick: (Int) -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isRead) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
            )
            .clickable {
                onClick.invoke(item.id)
            }
            .padding(16.dp),
    ) {
        Text(
            HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            color =
            if (isRead) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
        )

        val published = item.parsedPublished()

        if (published != null){
            Text(
                DateUtils.getRelativeTimeSpanString(published.time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString(),
                color =
                if (isRead) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
            )
        }


    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PromptListItem(
    prompt: ProposalPrompt,
    onItemActivate: () -> Unit,
    onItemEdit: (ProposalPrompt) -> Unit
){
    val expanded = remember {
        mutableStateOf(false)
    }

    val color = if (prompt.selected){
        MaterialTheme.colorScheme.primary
    } else MaterialTheme.colorScheme.background

    val contentColor = if (prompt.selected){
        MaterialTheme.colorScheme.onPrimary
    } else MaterialTheme.colorScheme.onBackground

    val caret: Int = if (expanded.value){
        com.ihfazh.notify.R.drawable.ic_baseline_keyboard_arrow_up_24
    } else {
        com.ihfazh.notify.R.drawable.ic_baseline_keyboard_arrow_down_24
    }

    val dismissState = rememberDismissState(DismissValue.Default){
        if (it == DismissValue.DismissedToEnd){
            onItemActivate.invoke()
            return@rememberDismissState false
        }

        if (it == DismissValue.DismissedToStart){
            onItemEdit.invoke(prompt)
            return@rememberDismissState false
        }

        true
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

            val backgroundColor by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> color
                    DismissValue.DismissedToEnd -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                    DismissValue.DismissedToStart -> androidx.compose.material3.MaterialTheme.colorScheme.secondary
                }
            )

            val icon = when(direction){
                DismissDirection.StartToEnd -> Icons.Default.Check
                DismissDirection.EndToStart -> Icons.Default.Edit
            }
            
            val scale by animateFloatAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 0f else 1.2f)

            val alignment = when(direction){
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }

            Box(
                Modifier.fillMaxSize().background(backgroundColor).padding(12.dp, 12.dp),
                contentAlignment = alignment
            ){
                Row{
                    if (direction == DismissDirection.EndToStart){
                        Text("Edit")
                        Spacer(Modifier.width(16.dp))
                    }
                    Icon(icon, contentDescription = "Hello world", modifier = Modifier.scale(scale))
                    if (direction == DismissDirection.StartToEnd){
                        Spacer(Modifier.width(16.dp))
                        Text("Activate")
                    }
                }


            }


        },
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        dismissThresholds = { FractionalThreshold(0.2f) }
    ) {

        Column(
            Modifier
                .background(color)
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    expanded.value = !expanded.value
                },
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    prompt.label,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = contentColor,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    maxLines = 2
                )

                Icon(
                    painter = painterResource(id = caret),
                    contentDescription = "Toggle",
                    tint = contentColor
                )
            }

            AnimatedVisibility(visible = expanded.value) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        prompt.text,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = contentColor
                    )
                }

            }
        }

    }

}

@Composable
@Preview
fun FeedListItemPreview(){
    Column() {
//        FeedListItem(item = SimpleFeedItem(1, "Hello world active"), onClick = {}, isRead = false)
//        FeedListItem(item = SimpleFeedItem(2, "Hello world non active"), onClick = {}, isRead = true)
    }
}