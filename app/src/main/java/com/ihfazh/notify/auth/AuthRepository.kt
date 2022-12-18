package com.ihfazh.notify.auth

interface AuthRepository {
    suspend fun getToken(): String?
    suspend fun login(username: String, password: String): LoginStatus
}