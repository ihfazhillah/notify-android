package com.ihfazh.notify.prompt_create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PromptCreateViewModel: ViewModel() {
    private val _label = MutableStateFlow("")
    val label = _label.asStateFlow()
    fun setLabel(value: String){
        _label.value = value
    }

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()
    fun setText(value: String){
        _text.value = value
    }

    private val _selected = MutableStateFlow(false)
    val selected = _selected.asStateFlow()
    fun setSelected(value: Boolean){
        _selected.value = value
    }

    fun validate(): Boolean{
        return false
    }

    fun submit(){

    }

}