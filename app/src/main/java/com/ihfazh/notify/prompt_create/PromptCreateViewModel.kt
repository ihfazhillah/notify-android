package com.ihfazh.notify.prompt_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihfazh.notify.common.SourceResult
import com.ihfazh.notify.prompt.PromptRepository
import com.ihfazh.notify.prompt.ProposalPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PromptCreateViewModel(
    private val promptRepository: PromptRepository
): ViewModel() {
    private val _label = MutableStateFlow("")
    val label = _label.asStateFlow()
    fun setLabel(value: String){
        _label.value = value
    }

    private val _labelError = MutableStateFlow("")
    val labelError = _labelError.asStateFlow()


    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()
    fun setText(value: String){
        _text.value = value
    }

    private val _textError = MutableStateFlow("")
    val textError = _textError.asStateFlow()

    private val _selected = MutableStateFlow(false)
    val selected = _selected.asStateFlow()
    fun setSelected(value: Boolean){
        _selected.value = value
    }

    fun validate(): Boolean{
        var valid = true

        if (text.value.isEmpty()){
            _textError.value = "The prompt text cannot be empty."
            valid = false
        }

        if (label.value.isEmpty()){
            _labelError.value = "The prompt label cannot be empty."
            valid = false
        }

        return valid
    }

    fun submit(next: (Boolean) -> Unit){
        viewModelScope.launch {
            val body = ProposalPrompt(
                -1,
                label.value,
                text.value,
                selected.value
            )

            val res = promptRepository.postProposalPrompt(body)

            with(Dispatchers.Main){
                next.invoke(res)
            }
        }
    }

    // preview feature
    private val _previewText = MutableStateFlow("")
    val previewText = _previewText.asStateFlow()

    private val _previewLoading = MutableStateFlow(false)
    val previewLoading = _previewLoading.asStateFlow()

    fun getPreview(text: String){
        _previewLoading.value = true

        viewModelScope.launch{
            val resp = promptRepository.preview(text)

            when(resp){
                is SourceResult.Error -> {
                    _previewText.value = resp.message!!
                }
                is SourceResult.Success -> {
                    val desc = "JobDesc: \n" + resp.data.jobDesc
                    val preview = "Preview: \n" + resp.data.preview

                    val data = "$desc\n\n$preview"
                    _previewText.value = data
                }
            }

            _previewLoading.value = false
        }

    }

}