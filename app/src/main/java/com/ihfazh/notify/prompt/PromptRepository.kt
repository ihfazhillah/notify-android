package com.ihfazh.notify.prompt

import androidx.paging.PagingData
import com.ihfazh.notify.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface PromptRepository {
    fun getProposalPrompts(): Flow<PagingData<ProposalPrompt>>
    suspend fun postProposalPrompt(body: ProposalPrompt): Boolean
}