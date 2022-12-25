package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class ProposalPromptListResponse (
    @Json(name="next")
    val next: String? = null,

    @Json(name="previous")
    val previous: String? = null,

    @Json(name="count")
    val count: Int,

    @Json(name="results")
    val results: List<ProposalPromptItemResponse> = emptyList()
)


data class ProposalPromptItemResponse(
    @Json(name="id")
    val id: Int,

    @Json(name="label")
    val label: String,

    @Json(name="text")
    val text: String,

    @Json(name="selected")
    val selected: Boolean = false,
)