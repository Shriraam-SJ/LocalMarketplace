package com.example.localmarketplace

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.google.android.gms.location.LocationServices

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmLocation)
        
        val mode = intent.getStringExtra("MODE") ?: "PICK"
        
        if (mode == "ROUTE") {
            btnConfirm.visibility = View.GONE
            setupRouteView()
        } else {
            btnConfirm.visibility = View.VISIBLE
            setupPickerMode(btnConfirm)
        }

        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
    }

    private fun setupPickerMode(btnConfirm: Button) {
        val maduraiPoint = Point.fromLngLat(78.1198, 9.9252)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(maduraiPoint)
                .zoom(12.0)
                .build()
        )

        btnConfirm.setOnClickListener {
            val center = mapView.mapboxMap.cameraState.center
            val resultIntent = android.content.Intent()
            resultIntent.putExtra("LATITUDE", center.latitude())
            resultIntent.putExtra("LONGITUDE", center.longitude())
            setResult(android.app.Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupRouteView() {
        val destLat = intent.getDoubleExtra("DEST_LAT", 0.0)
        val destLon = intent.getDoubleExtra("DEST_LON", 0.0)
        val destination = Point.fromLngLat(destLon, destLat)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val origin = Point.fromLngLat(location.longitude, location.latitude)
                addMarkers(origin, destination)
                
                val points = listOf(origin, destination)
                mapView.mapboxMap.setCamera(
                    mapView.mapboxMap.cameraForCoordinates(points, EdgeInsets(200.0, 200.0, 200.0, 200.0))
                )
            } else {
                Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show()
                // Show at least the destination
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder().center(destination).zoom(14.0).build()
                )
                addMarkers(null, destination)
            }
        }
    }

    private fun addMarkers(origin: Point?, destination: Point) {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        
        // Destination Marker
        val destOptions = PointAnnotationOptions()
            .withPoint(destination)
            .withIconImage(bitmapFromDrawableRes(R.drawable.ic_marketproducts)!!)
        pointAnnotationManager.create(destOptions)

        // Origin Marker (Current Location)
        if (origin != null) {
            val originOptions = PointAnnotationOptions()
                .withPoint(origin)
                .withIconImage(bitmapFromDrawableRes(android.R.drawable.ic_menu_mylocation)!!)
            pointAnnotationManager.create(originOptions)
        }
    }

    private fun bitmapFromDrawableRes(resourceId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, resourceId)
        if (drawable is BitmapDrawable) return drawable.bitmap
        
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
