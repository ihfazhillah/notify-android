package com.ihfazh.notify.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.ihfazh.notify.auth.AuthRepository
import com.ihfazh.notify.common.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber

@KoinViewModel
class HomeScreenViewModel(
    private val preferenceManager: PreferenceManager,
    private val repository: AuthRepository,
): ViewModel() {
    fun getToken(): String? = preferenceManager.getToken()

    private val _notificationPermissionGranted = MutableStateFlow(false)
    val notificationPermissionGranted = _notificationPermissionGranted.asStateFlow()
    fun setNotificationPermissionGranted(value: Boolean){
        _notificationPermissionGranted.value = value
    }

    private val _notificationPermissionText = MutableStateFlow("")
    val notificationPermissionText = _notificationPermissionText.asStateFlow()
    fun setNotificationPermissionText(value: String){
        _notificationPermissionText.value = value
    }

    fun registerDevice() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Timber.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Timber.d("token : $token")
            FirebaseInstallations.getInstance().id.addOnSuccessListener { id ->
                viewModelScope.launch {
                    repository.registerDevice(id, token)
                }

            }
            // Log and toast
        })
    }
}