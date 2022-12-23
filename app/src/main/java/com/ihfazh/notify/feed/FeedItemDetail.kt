package com.ihfazh.notify.feed

data class FeedItemDetail(
    val id: Int,
    val guid: String,
    val title: String,
    val tags: List<String> = emptyList(),
    val description: String,
    val category: String?,
    val country: String?,
    val proposalExample: String?,
    val skills: List<String>,
    val budget: Int? = null,
    val hourlyRange: List<Int>? = null
)
