package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json


data class ProposalPromptPreviewResponse(
    @Json(name="jobDesc")
    val jobDesc: String,
    @Json(name="proposal")
    val proposal: String,
)

