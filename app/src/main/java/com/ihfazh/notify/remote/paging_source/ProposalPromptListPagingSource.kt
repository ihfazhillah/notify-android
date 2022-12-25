package com.ihfazh.notify.remote.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ihfazh.notify.prompt.ProposalPrompt
import com.ihfazh.notify.remote.ApiResult
import com.ihfazh.notify.remote.NotifyService
import com.ihfazh.notify.remote.safeApiRequest

class ProposalPromptListPagingSource(
    private val remote: NotifyService,
) : PagingSource<Int, ProposalPrompt>() {
    override fun getRefreshKey(state: PagingState<Int, ProposalPrompt>): Int? {
        return state.anchorPosition?.let { pos ->
            val anchorPage = state.closestPageToPosition(pos)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProposalPrompt> {
        val nextPageNumber = params.key ?: 1
        val response = safeApiRequest {
            remote.getProposalPrompts(nextPageNumber)
        }

        return when(response){
            is ApiResult.Error -> {
                LoadResult.Error(Exception(response.message))
            }
            is ApiResult.Success -> {
                LoadResult.Page(
                    response.data.results.map{ProposalPrompt(it.id, it.label, it.text, it.selected)},
                    prevKey = if (response.data.previous != null) nextPageNumber - 1 else null,
                    nextKey = if (response.data.next != null) nextPageNumber + 1 else null,
                )
            }
        }
    }
}