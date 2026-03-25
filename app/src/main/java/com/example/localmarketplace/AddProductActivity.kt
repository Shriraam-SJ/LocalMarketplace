package com.example.localmarketplace

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    private val CHANNEL_ID = "product_addition_channel"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val mapResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val lat = result.data?.getDoubleExtra("LATITUDE", 0.0) ?: 0.0
                val lon = result.data?.getDoubleExtra("LONGITUDE", 0.0) ?: 0.0
                updateLocationFromCoordinates(lat, lon)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        askNotificationPermission()
        createNotificationChannel()

        val etName = findViewById<EditText>(R.id.etProductName)
        val etPrice = findViewById<EditText>(R.id.etProductPrice)
        val etLocation = findViewById<EditText>(R.id.etProductLocation)
        val tilLocation = findViewById<TextInputLayout>(R.id.tilLocation)
        val btnPickOnMap = findViewById<Button>(R.id.btnPickOnMap)
        val btnAdd = findViewById<Button>(R.id.btnAddProduct)

        tilLocation.setEndIconOnClickListener {
            checkLocationPermissions()
        }

        btnPickOnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            mapResultLauncher.launch(intent)
        }

        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val rawPrice = etPrice.text.toString()
            val locationText = etLocation.text.toString()

            if (name.isNotEmpty() && rawPrice.isNotEmpty() && locationText.isNotEmpty()) {
                val geojson = if (selectedLatitude != null && selectedLongitude != null) {
                    Location(coordinates = listOf(selectedLongitude!!, selectedLatitude!!))
                } else null

                val product = Product(
                    name = name,
                    price = "₹$rawPrice",
                    location = locationText,
                    geojson = geojson
                )
                
                ProductRepository.addProduct(product) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Product Added: $name", Toast.LENGTH_SHORT).show()
                            showNotification(name)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                updateLocationFromCoordinates(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocationFromCoordinates(lat: Double, lon: Double) {
        selectedLatitude = lat
        selectedLongitude = lon
        
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0].getAddressLine(0)
                findViewById<EditText>(R.id.etProductLocation).setText(address)
            } else {
                findViewById<EditText>(R.id.etProductLocation).setText("$lat, $lon")
            }
        } catch (e: Exception) {
            findViewById<EditText>(R.id.etProductLocation).setText("$lat, $lon")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Product Addition"
            val descriptionText = "Notifications for product addition"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(productName: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_marketproducts)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Product '$productName' added successfully")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}
