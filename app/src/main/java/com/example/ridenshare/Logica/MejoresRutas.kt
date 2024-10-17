package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ridenshare.R
import com.google.android.gms.maps.model.LatLng

class MejoresRutas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RutasAdapter

    // Definición de los puntos específicos de cada ruta
    private val rutaCosteraPuntos = listOf(
        LatLng(10.400431, -75.516490), // Centro de Cartagena
        LatLng(10.423196, -75.547253), // Castillo San Felipe de Barajas
        LatLng(10.428509, -75.541534), // La Popa
        LatLng(10.475027, -75.509867), // Playa Blanca
        LatLng(10.477251, -75.511532), // Barú
        LatLng(10.417291, -75.549365), // Boca Grande
        LatLng(10.400431, -75.516490)  // Vuelta al Centro de Cartagena
    )

    private val rutaMontanaClasicaPuntos = listOf(
        LatLng(6.244203, -75.581215), // Centro de Medellín
        LatLng(6.290200, -75.558103), // Parque Arví
        LatLng(6.315299, -75.531295), // Alto de Las Palmas
        LatLng(6.230833, -75.590553), // Cerro Nutibara
        LatLng(6.244203, -75.581215)  // Vuelta al Centro de Medellín
    )

    private val recorridoUrbanoPuntos = listOf(
        LatLng(4.598056, -74.075833), // Plaza de Bolívar
        LatLng(4.605145, -74.072093), // Museo del Oro
        LatLng(4.624335, -74.063644), // Universidad Nacional
        LatLng(4.668611, -74.057778), // Parque Simón Bolívar
        LatLng(4.598056, -74.075833)  // Vuelta a Plaza de Bolívar
    )

    // Creación de la lista de rutas usando los puntos específicos
    private val rutasList = listOf(
        Ruta("Ruta Costera", 15.2, "1h 50m", rutaCosteraPuntos),
        Ruta("Montaña Clásica", 22.4, "2h 30m", rutaMontanaClasicaPuntos),
        Ruta("Recorrido Urbano", 8.9, "1h 10m", recorridoUrbanoPuntos)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mejores_rutas)

        recyclerView = findViewById(R.id.recyclerViewBestRoutes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RutasAdapter(rutasList) { ruta ->
            openMapWithRoute(ruta)
        }
        recyclerView.adapter = adapter
    }

    private fun openMapWithRoute(ruta: Ruta) {
        val intent = Intent(this, MapRouteActivity::class.java)
        intent.putExtra("routeName", ruta.nombre)
        intent.putExtra("routePoints", ArrayList(ruta.puntos)) // Pasar los puntos
        startActivity(intent)
    }
}
