package com.ihfazh.notify.feed

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeedItems(): Flow<PagingData<SimpleFeedItem>>
}