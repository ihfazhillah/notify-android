package com.ihfazh.notify.home

import androidx.lifecycle.ViewModel
import com.ihfazh.notify.common.PreferenceManager
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    private val preferenceManager: PreferenceManager
): ViewModel() {
    fun getToken(): String? = preferenceManager.getToken()
}