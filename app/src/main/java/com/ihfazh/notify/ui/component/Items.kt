package com.ihfazh.notify.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.ihfazh.notify.feed.SimpleFeedItem
import com.ihfazh.notify.prompt.ProposalPrompt

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

@Composable
fun PromptListItem(
    prompt: ProposalPrompt
){
    val expanded = remember {
        mutableStateOf(false)
    }
    Column(
        Modifier
            .padding(16.dp)
            .clickable {
                expanded.value = !expanded.value
            }
    ){
        val active = if (prompt.selected) "Selected" else "Not Selected"
        Text(prompt.label, fontSize = MaterialTheme.typography.titleMedium.fontSize)
        Text(active, fontSize = MaterialTheme.typography.labelMedium.fontSize)

        if (expanded.value){
            Spacer(Modifier.height(8.dp))
            Text(prompt.text, fontSize = MaterialTheme.typography.bodyMedium.fontSize)
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