package com.ihfazh.notify.item_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER

@Destination(
    route="item_detail",
    deepLinks = [
        DeepLink(uriPattern = "https://notify.ihfazh.com/deeplink/$FULL_ROUTE_PLACEHOLDER")
    ]
)
@Composable
fun ItemDetail(
    id: Int

){
    NotifyTheme {
        Column {
            Text(text = "Hello world, detail for $id")
        }
    }
}