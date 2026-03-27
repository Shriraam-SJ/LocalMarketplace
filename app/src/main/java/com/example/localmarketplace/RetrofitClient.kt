package com.example.localmarketplace

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // baseurl1: Local host for testing (10.0.2.2 is the alias for localhost in Android Emulator)
    private const val BASE_URL_LOCAL = "http://10.0.2.2:3000/"
    
    // baseurl2: Render URL for production
    private const val BASE_URL_RENDER = "https://localmarketplace-mwzj.onrender.com/"

    // Toggle between BASE_URL_LOCAL and BASE_URL_RENDER as needed
    private const val BASE_URL = BASE_URL_LOCAL

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
