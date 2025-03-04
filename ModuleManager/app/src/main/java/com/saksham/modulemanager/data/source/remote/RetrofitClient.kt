package com.saksham.modulemanager.data.source.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit client for API requests
 */
object RetrofitClient {
    private const val GITHUB_API_BASE_URL = "https://api.github.com/"
    
    /**
     * Create GitHub API service
     */
    fun createGithubApiService(): GithubApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(GITHUB_API_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(GithubApiService::class.java)
    }
    
    /**
     * Create OkHttp client with logging and timeout configuration
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
