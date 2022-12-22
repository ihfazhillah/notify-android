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

    @Json(name="description")
    val description: String,

    @Json(name="category")
    val category: String,

    @Json(name="country")
    val country: String,

    @Json(name="proposal_example_text")
    val proposal_example_text: String? = null,

    @Json(name="skills")
    val skills: List<String> = emptyList(),

    @Json(name="budget")
    val budget: Int? = null,

    @Json(name="hourly_range")
    val hourly_range: List<Int>? = null,

    @Json(name="tags")
    val tags: List<TagsItem> = emptyList()
)

