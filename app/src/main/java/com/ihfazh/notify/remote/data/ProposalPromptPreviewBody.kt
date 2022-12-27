package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json


data class ProposalPromptPreviewBody(
    @Json(name="text")
    val text: String,
)

