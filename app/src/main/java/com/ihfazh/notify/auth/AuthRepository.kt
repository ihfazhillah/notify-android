package com.ihfazh.notify.auth

interface AuthRepository {
    suspend fun getToken(): String?
    suspend fun login(username: String, password: String): LoginStatus
    suspend fun registerDevice(deviceId: String, token: String)
}