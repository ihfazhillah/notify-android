package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json


data class GenerateProposalResponse(
    @Json(name="proposal")
    val proposal: String,
)

