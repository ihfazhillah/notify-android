package com.ihfazh.notify.feed_detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.feed.FeedItemDetail
import com.ihfazh.notify.feed.FeedRepository
import com.ihfazh.notify.prompt.PromptRepository
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
    private val feedRepository: FeedRepository,
    private val promptRepository: PromptRepository
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

    private val _summarizeLoading = MutableStateFlow(false)
    val summarizeLoading = _summarizeLoading.asStateFlow()

    private val _summarizeString = MutableStateFlow("")
    val summarizeString = _summarizeString.asStateFlow()

    fun getSummarize(text: String){
        _summarizeLoading.value = true
        viewModelScope.launch {
            when(val resp = promptRepository.generateGeneralPrompt("summarize", text)){
                is SourceResult.Error -> {
                    _summarizeString.value = "ERRRORRR"
                }
                is SourceResult.Success -> {
                    _summarizeString.value = resp.data
                }
            }

            _summarizeLoading.value = false
        }
    }

    private val _teaserLoading = MutableStateFlow(false)
    val teaserLoading = _teaserLoading.asStateFlow()

    private val _teaserString = MutableStateFlow("")
    val teaserString = _teaserString.asStateFlow()

    fun getTeaser(text: String){
        _teaserLoading.value = true
        viewModelScope.launch {
            when(val resp = promptRepository.generateGeneralPrompt("teaser", text)){
                is SourceResult.Error -> {
                    _teaserString.value = "ERRRORRR"
                }
                is SourceResult.Success -> {
                    _teaserString.value = resp.data
                }
            }

            _teaserLoading.value = false
        }
    }

    private val _questionLoading = MutableStateFlow(false)
    val questionLoading = _questionLoading.asStateFlow()

    private val _questionString = MutableStateFlow("")
    val questionString = _questionString.asStateFlow()

    fun getQuestion(text: String){
        _questionLoading.value = true
        viewModelScope.launch {
            when(val resp = promptRepository.generateGeneralPrompt("question", text)){
                is SourceResult.Error -> {
                    _questionString.value = "ERRRORRR"
                }
                is SourceResult.Success -> {
                    _questionString.value = resp.data
                }
            }

            _questionLoading.value = false
        }
    }

    private val _suggestionLoading = MutableStateFlow(false)
    val suggestionLoading = _suggestionLoading.asStateFlow()

    private val _suggestionString = MutableStateFlow("")
    val suggestionString = _suggestionString.asStateFlow()

    fun getSuggestion(text: String){
        _suggestionLoading.value = true
        viewModelScope.launch {
            when(val resp = promptRepository.generateGeneralPrompt("suggestion", text)){
                is SourceResult.Error -> {
                    _suggestionString.value = "ERRRORRR"
                }
                is SourceResult.Success -> {
                    _suggestionString.value = resp.data
                }
            }

            _suggestionLoading.value = false
        }
    }

    private val _keyPointsLoading = MutableStateFlow(false)
    val keyPointsLoading = _keyPointsLoading.asStateFlow()

    private val _keyPointsString = MutableStateFlow("")
    val keyPointsString = _keyPointsString.asStateFlow()

    fun getKeyPoints(text: String){
        _keyPointsLoading.value = true
        viewModelScope.launch {
            when(val resp = promptRepository.generateGeneralPrompt("keyPoints", text)){
                is SourceResult.Error -> {
                    _keyPointsString.value = "ERRRORRR"
                }
                is SourceResult.Success -> {
                    _keyPointsString.value = resp.data
                }
            }

            _keyPointsLoading.value = false
        }
    }
}