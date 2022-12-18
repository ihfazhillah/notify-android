package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class GetTokenBody(
    @Json(name="username")
    val username: String,
    @Json(name="password")
    val password: String
)

