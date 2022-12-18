package com.ihfazh.notify.remote

import com.ihfazh.notify.auth.AuthRepository
import com.ihfazh.notify.auth.LoginStatus
import com.ihfazh.notify.common.PreferenceManager
import com.ihfazh.notify.remote.data.GetTokenBody
import org.koin.core.annotation.Factory

@Factory
class DefaultAuthRepository(
    private val preferenceManager: PreferenceManager,
    private val api: NotifyService
): AuthRepository {
    override suspend fun getToken(): String? {
        return preferenceManager.getToken()
    }

    override suspend fun login(username: String, password: String): LoginStatus {
        val resp = safeApiRequest {
            api.getToken(GetTokenBody(username, password))
        }

        return when(resp){
            is ApiResult.Error -> LoginStatus.Error
            is ApiResult.Success -> LoginStatus.Success(resp.data.token)
        }
    }
}