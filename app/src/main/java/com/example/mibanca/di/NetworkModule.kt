package com.example.mibanca.di

import com.example.mibanca.network.AuthInterceptor
import com.example.mibanca.network.BankApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "https://api-paoxql37vq-uc.a.run.app/"
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte el JSON a tus Data Classes
            .build()
    }

    val apiService: BankApiService by lazy {
        retrofit.create(BankApiService::class.java)
    }
}