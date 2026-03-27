package com.example.localmarketplace

import retrofit2.Call
import retrofit2.http.*

data class AuthResponse(val message: String, val user: User?)
data class User(val name: String? = null, val email: String, val password: String? = null)

interface ApiService {
    @POST("api/auth/signup")
    fun signup(@Body user: User): Call<AuthResponse>

    @POST("api/auth/login")
    fun login(@Body user: User): Call<AuthResponse>

    @GET("api/products/ids")
    fun getProductIds(): Call<List<String>>

    @GET("api/products/{id}")
    fun getProductById(@Path("id") id: String): Call<Product>

    @GET("api/products")
    fun getProducts(): Call<List<Product>>

    @POST("api/products")
    fun addProduct(@Body product: Product): Call<Product>

    @DELETE("api/products/{id}")
    fun deleteProduct(@Path("id") id: String): Call<Void>
}
