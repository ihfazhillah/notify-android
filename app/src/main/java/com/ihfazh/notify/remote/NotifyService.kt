package com.ihfazh.notify.remote

import com.ihfazh.notify.remote.data.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotifyService {
    @POST("auth-token/")
    suspend fun getToken(@Body body: GetTokenBody): GetTokenResponse

    @POST("api/devices/")
    suspend fun registerDevice(@Body body: RegisterDeviceBody): RegisterDeviceResponse

    @GET("api/feed-items/")
    suspend fun getFeedItems(@Query("page")  page: Int): FeedListResponse

    @GET("api/feed-items/{id}/")
    suspend fun getFeedItem(@Path("id") id: Int): FeedItemResponse

    @GET("api/feed-items/{id}/log_access/")
    suspend fun logFeedItem(@Path("id") id: Int)
}