package com.ihfazh.notify.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PromptListItem(
    prompt: ProposalPrompt,
    onItemActivate: () -> Unit
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

        if (expanded.value){
            Spacer(Modifier.height(8.dp))
            Text(prompt.text, fontSize = MaterialTheme.typography.bodyMedium.fontSize, color = contentColor)
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth()){
                if (!prompt.selected){
                    Button(onClick = {
                        onItemActivate.invoke()
                    }) {
                        Text("Activate")
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun FeedListItemPreview(){
    Column() {
        FeedListItem(item = SimpleFeedItem(1, "Hello world active"), onClick = {}, isRead = false)
        FeedListItem(item = SimpleFeedItem(2, "Hello world non active"), onClick = {}, isRead = true)
    }
}