package com.ihfazh.notify

import com.ihfazh.notify.common.PreferenceManager
import com.ihfazh.notify.remote.NotifyService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@ComponentScan("com.ihfazh.notify")
class NotifyKoinApplication {
    @Single
    fun setOkHttpClient(preferenceManager: PreferenceManager): OkHttpClient {
        val interceptor =  HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        val authInterceptor = Interceptor {
            val request = it.request()
            val token = preferenceManager.getToken()
            if (token != null){
                val nextRequest = request.newBuilder()
                    .addHeader("Authorization", "Token ${preferenceManager.getToken()}")
                    .build()
                it.proceed(nextRequest)
            } else {

                it.proceed(request)
            }
        }

        return OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Factory
    fun setRetrofit(client: OkHttpClient, preferenceManager: PreferenceManager) : NotifyService{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://notify.ihfazh.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build();

        return retrofit.create(NotifyService::class.java);
    }
}