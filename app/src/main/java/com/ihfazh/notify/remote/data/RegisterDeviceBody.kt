package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class RegisterDeviceBody(

	@Json(name="device_id")
	val device_id: String,

	@Json(name="name")
	val name: String,

	@Json(name="registration_id")
	val registration_id: String,

	@Json(name="active")
	val active: Boolean = true,

	@Json(name="type")
	val type: String = "android"
)
