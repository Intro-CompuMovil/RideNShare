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

class mapActivity : AppCompatActivity(), OnMapReadyCallback { // Cambia el nombre de la clase a MapActivity

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // Pasa la instancia de OnMapReadyCallback
    }

    override fun onMapReady(googleMap: GoogleMap) { // Marca correctamente el método
        map = googleMap
        createMarker()
    }

    private fun createMarker(){
        val coordinates = LatLng(4.62894444, -74.06485)
        val marker = MarkerOptions().position(coordinates).title("Universidad Javeriana")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates,14f),4000,null
        )
    }
}
