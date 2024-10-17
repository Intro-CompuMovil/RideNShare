package com.example.ridenshare.Logica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var routePoints: List<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Recupera los puntos de la ruta desde el intent
        routePoints = intent.getParcelableArrayListExtra("routePoints") ?: emptyList()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (routePoints.isNotEmpty()) {
            val firstPoint = routePoints.first()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 12f))

            val polylineOptions = PolylineOptions().clickable(false).addAll(routePoints)
            map.addPolyline(polylineOptions)

            for (point in routePoints) {
                map.addMarker(MarkerOptions().position(point))
            }
        }
    }
}
