package com.ihfazh.notify.prompt_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ihfazh.notify.auth.AuthRepository
import com.ihfazh.notify.common.PreferenceManager
import com.ihfazh.notify.feed.FeedRepository
import com.ihfazh.notify.prompt.PromptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PromptScreenViewModel(
    private val promptRepository: PromptRepository
): ViewModel() {

    val prompts = promptRepository.getProposalPrompts().shareIn(
        viewModelScope,
        SharingStarted.Lazily
    ).cachedIn(viewModelScope)

    fun select(id: Int){
        viewModelScope.launch {
            promptRepository.selectPrompt(id)
        }
    }

}
