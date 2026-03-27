package com.example.localmarketplace

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val product = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("PRODUCT", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("PRODUCT") as? Product
        }

        if (product != null) {
            findViewById<TextView>(R.id.tvDetailName).text = product.name
            findViewById<TextView>(R.id.tvDetailPrice).text = product.price
            findViewById<TextView>(R.id.tvDetailLocation).text = "Location: ${product.location}"
            findViewById<TextView>(R.id.tvDetailSeller).text = "Seller: ${product.seller}"

            val mediaItems = mutableListOf<MediaItem>()
            product.images.forEach { mediaItems.add(MediaItem(it, true)) }
            product.videos.forEach { mediaItems.add(MediaItem(it, false)) }

            val viewPager = findViewById<ViewPager2>(R.id.vpProductMedia)
            val tabLayout = findViewById<TabLayout>(R.id.tlMediaIndicator)

            if (mediaItems.isNotEmpty()) {
                viewPager.adapter = MediaPagerAdapter(mediaItems)
                TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
            }
        }
    }
}
