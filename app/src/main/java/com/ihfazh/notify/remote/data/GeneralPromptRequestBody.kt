package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class GeneralPromptRequestBody(
    // keep like this, until we change the backend to use type
    @Json(name="prompt")
    val prompt: Int,

    @Json(name="additional_body")
    val additional_body: String
)

