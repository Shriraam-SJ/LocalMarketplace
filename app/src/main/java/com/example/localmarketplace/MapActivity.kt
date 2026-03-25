package com.example.localmarketplace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmLocation)

        // Set default camera to Madurai
        val maduraiPoint = Point.fromLngLat(78.1198, 9.9252)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(maduraiPoint)
                .zoom(12.0)
                .build()
        )

        // The access token is read from strings.xml (mapbox_access_token)
        // by the Mapbox SDK automatically if not provided in MapInitOptions.
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)

        btnConfirm.setOnClickListener {
            val center = mapView.mapboxMap.cameraState.center
            val resultIntent = Intent()
            resultIntent.putExtra("LATITUDE", center.latitude())
            resultIntent.putExtra("LONGITUDE", center.longitude())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
