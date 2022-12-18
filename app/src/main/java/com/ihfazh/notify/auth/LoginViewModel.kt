package com.ihfazh.notify.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihfazh.notify.common.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val repository: AuthRepository,
    private val preferenceManager: PreferenceManager
): ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    fun setUsername(value: String){
        _username.value = value
    }

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    fun setPassword(value: String){
        _password.value = value
    }

    private val _errorString = MutableStateFlow("")
    val errorString = _errorString.asStateFlow()

    fun login(listener: (Boolean) -> Unit){
        viewModelScope.launch {
            when(val resp = repository.login(username.value, password.value)){
                LoginStatus.Error -> {
                    _errorString.value = "Login Failed. Check username or password."
                    withContext(Dispatchers.Main){
                        listener.invoke(false)
                    }
                }
                is LoginStatus.Success -> {
                    withContext(Dispatchers.Main){
                        preferenceManager.setToken(resp.token)
                        listener.invoke(true)
                    }
                }
            }
        }
    }
}