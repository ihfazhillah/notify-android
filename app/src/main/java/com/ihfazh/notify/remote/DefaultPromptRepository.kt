package com.ihfazh.notify.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.prompt.PromptPreview
import com.ihfazh.notify.prompt.PromptRepository
import com.ihfazh.notify.prompt.ProposalPrompt
import com.ihfazh.notify.remote.data.ProposalPromptCreateBody
import com.ihfazh.notify.remote.data.ProposalPromptPreviewBody
import com.ihfazh.notify.remote.data.ProposalPromptUpdateBody
import com.ihfazh.notify.remote.paging_source.ProposalPromptListPagingSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import timber.log.Timber

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

    override suspend fun preview(text: String): SourceResult<PromptPreview> {
        val resp = safeApiRequest {
            api.getProposalPromptPreview(ProposalPromptPreviewBody(text))
        }


        return when(resp){
            is ApiResult.Error -> SourceResult.Error("Something error with API")
            is ApiResult.Success -> SourceResult.Success(
                PromptPreview(resp.data.jobDesc, resp.data.proposal)
            )
        }
    }

    override suspend fun updateProposalPrompt(body: ProposalPrompt): Boolean {
        val resp = safeApiRequest {
            api.updateProposalPrompt(
                body.id,
                body.asUpdateBody()
            )
        }

        return when(resp){
            is ApiResult.Error -> false
            is ApiResult.Success -> true
        }
    }
}

private fun ProposalPrompt.asUpdateBody(): ProposalPromptUpdateBody {
    return ProposalPromptUpdateBody(
        id,
        label,
        text,
        selected
    )

}

private fun ProposalPrompt.asBody(): ProposalPromptCreateBody {
    return ProposalPromptCreateBody(label, text, selected)
}
