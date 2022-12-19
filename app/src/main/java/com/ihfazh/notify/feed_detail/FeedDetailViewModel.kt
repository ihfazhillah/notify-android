package com.ihfazh.notify.feed_detail

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


sealed class FeedState{
    object Empty: FeedState()
    object Loading: FeedState()
    data class Error(val message: String): FeedState()
    data class Success(val item: FeedItemDetail): FeedState()
}

@KoinViewModel
class FeedDetailViewModel(
    private val feedRepository: FeedRepository
): ViewModel() {

    private val _feedState : MutableStateFlow<FeedState> = MutableStateFlow(FeedState.Empty)
    val feedState = _feedState.asStateFlow()


    fun log(id: Int){
        viewModelScope.launch {
            feedRepository.log(id)
        }

    }

    fun getFeed(id: Int) {
        _feedState.value = FeedState.Loading
        viewModelScope.launch{
            when(val resp = feedRepository.getFeedItem(id)){
                is SourceResult.Error -> {
                    _feedState.value = FeedState.Error(resp.message!!)
                }
                is SourceResult.Success -> {
                    _feedState.value = FeedState.Success(resp.data)
                }
            }
        }
    }
}