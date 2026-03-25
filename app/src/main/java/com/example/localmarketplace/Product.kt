package com.example.localmarketplace

import com.google.gson.annotations.SerializedName

data class Location(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
)

data class Product(
    @SerializedName("_id")
    val id: String? = null,
    val name: String,
    val price: String,
    val location: String, // String address
    val seller: String = "Me",
    val geojson: Location? = null
)
