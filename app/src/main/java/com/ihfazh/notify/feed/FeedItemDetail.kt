package com.ihfazh.notify.feed

data class FeedItemDetail(
    val id: Int,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList()
)
