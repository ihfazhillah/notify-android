package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class MyProposalResponse(
    @Json(name="pk")
    val pk: Int,

    @Json(name="text")
    val text: String
)

