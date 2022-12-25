package com.ihfazh.notify.prompt

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PromptRepository {
    fun getProposalPrompts(): Flow<PagingData<ProposalPrompt>>
}