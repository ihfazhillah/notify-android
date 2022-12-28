package com.ihfazh.notify.prompt

import androidx.paging.PagingData
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface PromptRepository {
    fun getProposalPrompts(): Flow<PagingData<ProposalPrompt>>
    suspend fun postProposalPrompt(body: ProposalPrompt): Boolean
    suspend fun selectPrompt(id: Int)
    suspend fun preview(text: String): SourceResult<PromptPreview>
    suspend fun updateProposalPrompt(body: ProposalPrompt): Boolean
}