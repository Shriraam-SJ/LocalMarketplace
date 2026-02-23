package com.example.localmarketplace

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class AddProductActivity : AppCompatActivity() {

    private val CHANNEL_ID = "product_addition_channel"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        askNotificationPermission()
        createNotificationChannel()

        val etName = findViewById<EditText>(R.id.etProductName)
        val etPrice = findViewById<EditText>(R.id.etProductPrice)
        val etLocation = findViewById<EditText>(R.id.etProductLocation)
        val btnAdd = findViewById<Button>(R.id.btnAddProduct)

        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val price = etProductPriceFormatted(etPrice.text.toString())
            val location = etLocation.text.toString()

            if (name.isNotEmpty() && price.isNotEmpty() && location.isNotEmpty()) {
                val product = Product(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    price = price,
                    location = location
                )
                ProductRepository.addProduct(product)

                Toast.makeText(this, "Product Added: $name", Toast.LENGTH_SHORT).show()
                showNotification(name)
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun etProductPriceFormatted(price: String): String {
        return if (price.isEmpty()) "" else "₹$price"
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
