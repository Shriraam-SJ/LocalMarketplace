package com.example.localmarketplace

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProductRepository {
    private val apiService = RetrofitClient.instance
    
    var selectedProduct: Product? = null

    fun addProduct(product: Product, onResult: (Boolean) -> Unit) {
        apiService.addProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                onResult(false)
            }
        })
    }

    fun getProductIds(onResult: (List<String>?) -> Unit) {
        apiService.getProductIds().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun getProductById(id: String, onResult: (Product?) -> Unit) {
        apiService.getProductById(id).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun getProducts(onResult: (List<Product>?) -> Unit) {
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun deleteProduct(id: String, onResult: (Boolean) -> Unit) {
        apiService.deleteProduct(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onResult(false)
            }
        })
    }
}
