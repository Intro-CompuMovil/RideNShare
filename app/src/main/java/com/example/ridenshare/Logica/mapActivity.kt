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

class mapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val ciudadLatLng = LatLng(4.62894444, -74.06485)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudadLatLng, 10f))


        createMarker()

        // Llama a mostrarRuta con una lista de puntos precargados
        val ruta = listOf(
            LatLng(4.62894444, -74.06485), // Universidad Javeriana
            LatLng(4.62994444, -74.06385), // Otro punto cercano para la simulación
            LatLng(4.63094444, -74.06285)  // Tercer punto cercano
        )
        mostrarRuta(ruta)


        val primerPunto = ruta[0]
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(primerPunto, 14f), 4000, null)
    }

    private fun createMarker() {
        val coordinates = LatLng(4.62894444, -74.06485)
        val marker = MarkerOptions().position(coordinates).title("Universidad Javeriana")
        map.addMarker(marker)
    }

    private fun mostrarRuta(ruta: List<LatLng>) {
        val polylineOptions = PolylineOptions().clickable(false)
        for (point in ruta) {
            polylineOptions.add(point)
        }
        map.addPolyline(polylineOptions)
    }
}
