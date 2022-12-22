package com.ihfazh.notify.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed.FeedRepository
import com.ihfazh.notify.feed.SimpleFeedItem
import com.ihfazh.notify.remote.data.FeedItemResponse
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
}