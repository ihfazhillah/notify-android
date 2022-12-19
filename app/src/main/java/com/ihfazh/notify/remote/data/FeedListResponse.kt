package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class FeedListResponse(

	@Json(name="next")
	val next: String? = null,

	@Json(name="previous")
	val previous: String? = null,

	@Json(name="count")
	val count: Int,

	@Json(name="results")
	val results: List<FeedListItem> = emptyList()
)

data class TagsItem(

	@Json(name="pk")
	val pk: Int? = null,

	@Json(name="title")
	val title: String,

	@Json(name="url")
	val url: String? = null
)

data class FeedListItem(

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
