package com.ihfazh.notify.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.ihfazh.notify.auth.AuthRepository
import com.ihfazh.notify.common.PreferenceManager
import com.ihfazh.notify.feed.FeedRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber

@KoinViewModel
class HomeScreenViewModel(
    private val preferenceManager: PreferenceManager,
    private val repository: AuthRepository,
    private val feedRepository: FeedRepository,
): ViewModel() {
    fun getToken(): String? = preferenceManager.getToken()

//    private val _notificationPermissionText = MutableStateFlow("")
//    val notificationPermissionText = _notificationPermissionText.asStateFlow()
//    fun setNotificationPermissionText(value: String){
//        _notificationPermissionText.value = value
//    }

    val feedItems = feedRepository.getFeedItems().shareIn(
        viewModelScope,
        SharingStarted.Lazily
    ).cachedIn(viewModelScope)

}