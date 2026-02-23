package com.example.localmarketplace

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val location: String,
    val seller: String = "Me"
)
