package com.example.localmarketplace

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLat: Double? = null
    private var userLon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val product = ProductRepository.selectedProduct

        if (product != null) {
            findViewById<TextView>(R.id.tvDetailName).text = product.name
            findViewById<TextView>(R.id.tvDetailPrice).text = product.price
            findViewById<TextView>(R.id.tvDetailLocation).text = product.location
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

            val btnLocate = findViewById<Button>(R.id.btnLocateOnMap)
            if (product.geojson != null && product.geojson.coordinates.size >= 2) {
                btnLocate.visibility = View.VISIBLE
                btnLocate.setOnClickListener {
                    val intent = Intent(this, MapActivity::class.java).apply {
                        putExtra("DEST_LAT", product.geojson.coordinates[1])
                        putExtra("DEST_LON", product.geojson.coordinates[0])
                        putExtra("MODE", "ROUTE")
                    }
                    startActivity(intent)
                }
                calculateDistance(product.geojson.coordinates[1], product.geojson.coordinates[0])
            } else {
                btnLocate.visibility = View.GONE
            }

        } else {
            finish()
        }
    }

    private fun calculateDistance(destLat: Double, destLon: Double) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLat = location.latitude
                userLon = location.longitude
                
                val results = FloatArray(1)
                Location.distanceBetween(userLat!!, userLon!!, destLat, destLon, results)
                val distanceInKm = results[0] / 1000
                
                val tvDistance = findViewById<TextView>(R.id.tvDistance)
                tvDistance.visibility = View.VISIBLE
                tvDistance.text = String.format(Locale.getDefault(), "%.1f km away from you", distanceInKm)
            }
        }
    }
}
