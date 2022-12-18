package com.ihfazh.notify.remote

import com.ihfazh.notify.remote.data.GetTokenBody
import com.ihfazh.notify.remote.data.GetTokenResponse
import com.ihfazh.notify.remote.data.RegisterDeviceBody
import com.ihfazh.notify.remote.data.RegisterDeviceResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface NotifyService {
    @POST("auth-token/")
    suspend fun getToken(@Body body: GetTokenBody): GetTokenResponse

    @POST("api/devices/")
    suspend fun registerDevice(@Body body: RegisterDeviceBody): RegisterDeviceResponse
}