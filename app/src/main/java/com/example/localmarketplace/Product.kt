package com.example.localmarketplace

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
) : Serializable

data class Product(
    @SerializedName("_id")
    val id: String? = null,
    val name: String,
    val price: String,
    val location: String, // String address
    val seller: String = "Me",
    val geojson: Location? = null,
    val images: List<String> = emptyList(), // Base64 strings
    val videos: List<String> = emptyList()  // Base64 strings
) : Serializable
