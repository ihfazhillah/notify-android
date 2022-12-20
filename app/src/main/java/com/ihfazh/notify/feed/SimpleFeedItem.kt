package com.ihfazh.notify.feed

data class SimpleFeedItem(
   val id: Int,
   val title: String,
   val accessed: Boolean = false
)