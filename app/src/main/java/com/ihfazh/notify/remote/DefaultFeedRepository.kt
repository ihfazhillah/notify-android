package com.ihfazh.notify.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed.FeedRepository
import com.ihfazh.notify.feed.SimpleFeedItem
import com.ihfazh.notify.proposal.MyProposalState
import com.ihfazh.notify.remote.data.FeedItemResponse
import com.ihfazh.notify.remote.data.MyProposalBody
import com.ihfazh.notify.remote.paging_source.FeedListPagingSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class DefaultFeedRepository(
    private val remote: NotifyService
): FeedRepository {
    override fun getFeedItems(): Flow<PagingData<SimpleFeedItem>> {
        val pager = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { FeedListPagingSource(remote) }
        )
        return pager.flow
    }

    override suspend fun getFeedItem(id: Int):  SourceResult<FeedItemDetail>{
        val resp = safeApiRequest {
            remote.getFeedItem(id)
        }

        return when(resp){
            is ApiResult.Error -> {
                SourceResult.Error(message = resp.message)
            }
            is ApiResult.Success -> {
                SourceResult.Success(FeedItemDetail(
                    resp.data.pk,
                    resp.data.guid,
                    resp.data.title,
                    resp.data.tags.map { it.title },
                    resp.data.description,
                    resp.data.category,
                    resp.data.country,
                    resp.data.proposal_example_text,
                    resp.data.skills,
                    resp.data.budget,
                    resp.data.hourly_range
                ))
            }
        }


    }

    override suspend fun log(id: Int): SourceResult<Boolean> {
        val resp = safeApiRequest {
            remote.logFeedItem(id)
        }
        return when(resp){
            is ApiResult.Error -> {
                SourceResult.Error(message = resp.message)
            }
            is ApiResult.Success -> {
                SourceResult.Success(true)
            }
        }
    }

    override suspend fun reloadProposalExample(id: Int): SourceResult<String> {
        val resp = safeApiRequest {
            remote.generateProposal(id)
        }

        return when(resp){
            is ApiResult.Error -> SourceResult.Error("Something bad happen")
            is ApiResult.Success -> SourceResult.Success(resp.data.proposal)
        }
    }

    override suspend fun loadMyProposal(id: Int): SourceResult<String> {
        return when(val resp = safeApiRequest { remote.getMyProposal(id) }){
            is ApiResult.Error -> {
                SourceResult.Error(resp.message)
            }
            is ApiResult.Success -> {
                SourceResult.Success(resp.data.text)
            }
        }
    }

    override suspend fun updateMyProposal(id: Int, text: String): SourceResult<MyProposalState> {
        return when(
            val resp = safeApiRequest { remote.updateMyProposal(id, MyProposalBody(text)) }
        ){
            is ApiResult.Error -> {
                SourceResult.Success(MyProposalState.Error(resp.message))
            }
            is ApiResult.Success -> {
                SourceResult.Success(
                    MyProposalState.Saved
                )
            }
        }

    }
}