package com.example.ridenshare.Logica

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ridenshare.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.ArrayList

class RutaTiempoRealActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var map: GoogleMap
    private val ruta = ArrayList<LatLng>()
    private var tiempoInicio: Long = 0
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta_tiempo_real)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val buttonIniciar: Button = findViewById(R.id.buttonIniciar)
        val buttonFinalizar: Button = findViewById(R.id.buttonFinalizar)

        buttonIniciar.setOnClickListener {
            checkLocationPermission()
        }

        buttonFinalizar.setOnClickListener {
            stopLocationUpdates()
            val distanciaTotal = calcularDistancia(ruta)
            val tiempoTotal = System.currentTimeMillis() - tiempoInicio

            guardarRuta(distanciaTotal, tiempoTotal, ruta)
            Toast.makeText(this, "Ruta finalizada y guardada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Ajuste inicial de la cámara con un zoom general de ciudad
        val ciudadLatLng = LatLng(4.62894444, -74.06485)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudadLatLng, 10f))
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 segundos
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    ruta.add(latLng)

                    // Muestra la ubicación actual en el mapa con un marcador y anima la cámara
                    map.addMarker(MarkerOptions().position(latLng).title("Ubicación Actual"))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))

                    // Dibuja la ruta en el mapa
                    mostrarRuta(ruta)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
            tiempoInicio = System.currentTimeMillis()
            Toast.makeText(this, "Ruta iniciada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            checkLocationPermission()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun mostrarRuta(ruta: List<LatLng>) {
        val polylineOptions = PolylineOptions().clickable(false).addAll(ruta)
        map.addPolyline(polylineOptions)
    }

    private fun calcularDistancia(puntos: List<LatLng>): Double {
        var distanciaTotal = 0.0
        for (i in 0 until puntos.size - 1) {
            val inicio = puntos[i]
            val fin = puntos[i + 1]
            val resultado = FloatArray(1)
            android.location.Location.distanceBetween(
                inicio.latitude, inicio.longitude,
                fin.latitude, fin.longitude,
                resultado
            )
            distanciaTotal += resultado[0]
        }
        return distanciaTotal
    }

    private fun guardarRuta(distancia: Double, tiempo: Long, puntos: List<LatLng>) {
        // Almacena la información en una base de datos local o remota
        // Implementa aquí el código necesario para guardar la ruta
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
