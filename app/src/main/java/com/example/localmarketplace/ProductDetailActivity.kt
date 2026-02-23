package com.example.localmarketplace

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val name = intent.getStringExtra("PRODUCT_NAME")
        val price = intent.getStringExtra("PRODUCT_PRICE")
        val location = intent.getStringExtra("PRODUCT_LOCATION")
        val seller = intent.getStringExtra("PRODUCT_SELLER")

        findViewById<TextView>(R.id.tvDetailName).text = name
        findViewById<TextView>(R.id.tvDetailPrice).text = price
        findViewById<TextView>(R.id.tvDetailLocation).text = "Location: $location"
        findViewById<TextView>(R.id.tvDetailSeller).text = "Seller: $seller"
    }
}
