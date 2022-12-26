package com.ihfazh.notify.feed

import androidx.paging.PagingData
import com.ihfazh.notify.common.SourceResult
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeedItems(): Flow<PagingData<SimpleFeedItem>>
    suspend fun getFeedItem(id: Int): SourceResult<FeedItemDetail>
    suspend fun log(id: Int): SourceResult<Boolean>
    suspend fun reloadProposalExample(id: Int): SourceResult<String>
}