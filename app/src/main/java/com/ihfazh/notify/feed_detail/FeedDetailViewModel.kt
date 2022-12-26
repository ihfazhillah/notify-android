package com.ihfazh.notify.feed_detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


sealed class FeedState<T>{
    class Empty<T>: FeedState<T>()
    class Loading<T>: FeedState<T>()
    data class Error<T>(val message: String): FeedState<T>()
    data class Success<T>(val item: FeedItemDetail): FeedState<T>()
}

@KoinViewModel
class FeedDetailViewModel(
    private val feedRepository: FeedRepository
): ViewModel() {

    private val _feedState : MutableStateFlow<FeedState<FeedItemDetail>> = MutableStateFlow(FeedState.Empty())
    val feedState = _feedState.asStateFlow()


    fun log(id: Int){
        viewModelScope.launch {
            feedRepository.log(id)
        }

    }

    fun getFeed(id: Int) {
        _feedState.value = FeedState.Loading()
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

    private val _example = MutableStateFlow("")
    val example = _example.asStateFlow()
    fun setExample(value: String){
        _example.value = value
    }

    // proposal screen
    private val _proposalLoading = MutableStateFlow(false)
    val proposalLoading = _proposalLoading.asStateFlow()

    fun loadProposal(id: Int){
        _proposalLoading.value = true
        viewModelScope.launch {

            when(val resp = feedRepository.reloadProposalExample(id)){
                is SourceResult.Error -> setExample("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR")
                is SourceResult.Success -> setExample(resp.data.trim())
            }

            _proposalLoading.value = false
        }

    }
}