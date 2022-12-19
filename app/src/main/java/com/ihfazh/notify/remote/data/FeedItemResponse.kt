package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class FeedItemResponse(

    @Json(name="guid")
    val guid: String,

    @Json(name="pk")
    val pk: Int,

    @Json(name="published")
    val published: String,

    @Json(name="title")
    val title: String,

    @Json(name="accessed")
    val accessed: Boolean,

    @Json(name="content")
    val content: String,

    @Json(name="tags")
    val tags: List<TagsItem> = emptyList()
)

