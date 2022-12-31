package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class GeneralPromptRequestResponse(
    @Json(name="pk")
    val pk: Int,
    @Json(name="prompt")
    val prompt: Int,
    @Json(name="additional_body")
    val additional_body: String,
    //             "duration",
    //            "error",
    //            "response"
    @Json(name="duration")
    val duration: String?,
    @Json(name="error")
    val error: String?,
    @Json(name="response")
    val response: CompletionResponse?,
)

