package com.ihfazh.notify.remote.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CompletionResponse(

	@Json(name="created")
	val created: Int? = null,

	@Json(name="usage")
	val usage: Usage? = null,

	@Json(name="model")
	val model: String? = null,

	@Json(name="id")
	val id: String? = null,

	@Json(name="choices")
	val choices: List<ChoicesItem?>? = null,

	@Json(name="object")
	val `object`: String? = null
) : Parcelable

@Parcelize
data class ChoicesItem(

	@Json(name="finish_reason")
	val finishReason: String? = null,

	@Json(name="index")
	val index: Int? = null,

	@Json(name="text")
	val text: String? = null,

) : Parcelable

@Parcelize
data class Usage(

	@Json(name="completion_tokens")
	val completionTokens: Int? = null,

	@Json(name="prompt_tokens")
	val promptTokens: Int? = null,

	@Json(name="total_tokens")
	val totalTokens: Int? = null
) : Parcelable
