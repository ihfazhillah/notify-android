package com.ihfazh.notify.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ihfazh.notify.feed.FeedRepository
import com.ihfazh.notify.feed.SimpleFeedItem
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
}