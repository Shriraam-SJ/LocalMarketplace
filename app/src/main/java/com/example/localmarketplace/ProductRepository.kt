package com.example.localmarketplace

object ProductRepository {
    private val products = mutableListOf<Product>()

    fun addProduct(product: Product) {
        products.add(product)
    }

    fun getProducts(): List<Product> {
        return products
    }

    fun deleteProduct(product: Product) {
        products.remove(product)
    }
}
