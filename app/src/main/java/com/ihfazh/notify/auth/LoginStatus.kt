package com.ihfazh.notify.auth

sealed class LoginStatus {
    data class Success(val token: String): LoginStatus()
    object Error: LoginStatus()
}
