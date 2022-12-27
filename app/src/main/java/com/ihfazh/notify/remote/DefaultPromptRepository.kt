package com.ihfazh.notify.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ihfazh.notify.prompt.PromptRepository
import com.ihfazh.notify.prompt.ProposalPrompt
import com.ihfazh.notify.remote.data.ProposalPromptCreateBody
import com.ihfazh.notify.remote.paging_source.ProposalPromptListPagingSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class DefaultPromptRepository(
    private val api: NotifyService
): PromptRepository {
    override fun getProposalPrompts(): Flow<PagingData<ProposalPrompt>> {
        val pager = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = {ProposalPromptListPagingSource(api)}
        )

        return pager.flow
    }

    override suspend fun postProposalPrompt(body: ProposalPrompt): Boolean {
        val resp = safeApiRequest {
            api.postProposalPrompt(
                body.asBody()
            )
        }

        return when(resp){
            is ApiResult.Error -> false
            is ApiResult.Success -> true
        }
    }

    override suspend fun selectPrompt(id: Int) {
        safeApiRequest {
            api.selectPrompt(id)
        }
    }
}

private fun ProposalPrompt.asBody(): ProposalPromptCreateBody {
    return ProposalPromptCreateBody(label, text, selected)
}
