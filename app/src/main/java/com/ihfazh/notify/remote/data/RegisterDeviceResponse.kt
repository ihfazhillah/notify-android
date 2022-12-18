package com.ihfazh.notify.remote.data

import com.squareup.moshi.Json

data class RegisterDeviceResponse(

	@Json(name="device_id")
	val deviceId: String,

	@Json(name="date_created")
	val dateCreated: String,

	@Json(name="name")
	val name: String,

	@Json(name="registration_id")
	val registrationId: String,

	@Json(name="active")
	val active: Boolean,

	@Json(name="id")
	val id: Int,

	@Json(name="type")
	val type: String
)
