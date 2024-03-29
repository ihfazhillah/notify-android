package com.ihfazh.notify.common

import android.content.Context
import androidx.core.content.edit
import com.ihfazh.notify.R
import org.koin.core.annotation.Single

@Single
class PreferenceManager(context: Context) {
    companion object {
        private const val USER_TOKEN = "user_token"
        private const val REGISTRATION_ID = "registration_id"
    }

    private val prefs = context.getSharedPreferences(
        context.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )


    fun getToken(): String? =
        prefs.getString(USER_TOKEN, null)

    fun setToken(token: String?) {
        prefs.edit {
            putString(USER_TOKEN, token)
            apply()
        }
    }

    fun setRegistrationId(token: String){
        prefs.edit {
            putString(REGISTRATION_ID, token)
            apply()
        }
    }

    fun getRegistrationId(): String? = prefs.getString(REGISTRATION_ID, null)
}