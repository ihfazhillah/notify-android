package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class GetTokenResponse(
    @Json(name="token")
    val token: String
)
