package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.Data.DataTest
import com.example.ridenshare.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class RutaDesdeMapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val ruta = ArrayList<LatLng>()
    private var distanciaTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta_desde_mapa)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            guardarRuta("Ruta desde el Mapa", distanciaTotal, "1h 20m", ruta)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val ciudadLatLng = LatLng(4.62894444, -74.06485) // Punto inicial general
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudadLatLng, 10f))

        map.setOnMapClickListener { latLng ->
            // Agrega el marcador y el punto a la ruta
            map.addMarker(MarkerOptions().position(latLng).title("Punto de la Ruta"))
            if (ruta.isNotEmpty()) {
                distanciaTotal += calcularDistancia(ruta.last(), latLng)
            }
            ruta.add(latLng)
            mostrarRuta()
        }
    }

    private fun mostrarRuta() {
        val polylineOptions = PolylineOptions().clickable(false).addAll(ruta)
        map.addPolyline(polylineOptions)
        Toast.makeText(this, "Distancia Total: ${String.format("%.2f", distanciaTotal)} km", Toast.LENGTH_SHORT).show()
    }

    private fun calcularDistancia(punto1: LatLng, punto2: LatLng): Double {
        val resultado = FloatArray(1)
        android.location.Location.distanceBetween(
            punto1.latitude, punto1.longitude,
            punto2.latitude, punto2.longitude,
            resultado
        )
        return resultado[0] / 1000.0 // Devuelve la distancia en kilómetros
    }

    override fun onDestroy() {
        super.onDestroy()
        guardarRuta("Ruta desde el Mapa", distanciaTotal, "1h 20m", ruta)
    }

    private fun guardarRuta(nombre: String, distancia: Double, tiempo: String, puntos: List<LatLng>) {
        val route = DataTest.Route("", mutableListOf())
        for (point in puntos){
            route.points.add(DataTest.Point(point.latitude, point.longitude))
        }
        DataTest.routes.add(route)

        Toast.makeText(this, "Ruta finalizada y guardada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, CrearRutaActivity::class.java))
    }

}