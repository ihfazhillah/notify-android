package com.ihfazh.notify.remote

import com.ihfazh.notify.prompt.ProposalPrompt
import com.ihfazh.notify.remote.data.*
import retrofit2.http.*

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

    @POST("api/feed-items/{id}/generate_proposal/")
    suspend fun generateProposal(@Path("id") id: Int): GenerateProposalResponse

    @GET("api/proposal-prompts/")
    suspend fun getProposalPrompts(
        @Query("page") page: Int
    ): ProposalPromptListResponse

    @POST("api/proposal-prompts/")
    suspend fun postProposalPrompt(
        @Body body: ProposalPromptCreateBody
    ): ProposalPromptItemResponse

    @POST("api/proposal-prompts/{id}/activate/")
    suspend fun selectPrompt(@Path("id") id: Int)

    @PUT("api/proposal-prompts/{id}/")
    suspend fun updateProposalPrompt(@Path("id") id: Int, @Body body: ProposalPromptUpdateBody): ProposalPromptItemResponse

    @POST("api/proposal-prompts/preview/")
    suspend fun getProposalPromptPreview(@Body body: ProposalPromptPreviewBody): ProposalPromptPreviewResponse
}