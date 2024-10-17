package com.example.ridenshare.Logica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ridenshare.R

class myRoutes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rutasAdapter: RutasAdapter
    private val rutas = mutableListOf<Ruta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_routes)

        // Inicializa el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRutas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar rutas (simulando obtenerlas de una base de datos o almacenamiento local)
        cargarRutas()

        // Configura el adaptador
        rutasAdapter = RutasAdapter(rutas)
        recyclerView.adapter = rutasAdapter
    }

    private fun cargarRutas() {
        val sharedPreferences = getSharedPreferences("RUTAS", MODE_PRIVATE)
        val rutasGuardadas = sharedPreferences.all

        for ((key, value) in rutasGuardadas) {
            val rutaData = (value as String).split(";")
            val nombre = rutaData[0]
            val distancia = rutaData[1].toDouble()
            val tiempo = rutaData[2]

            val ruta = Ruta(nombre, distancia, tiempo, emptyList()) // Los puntos pueden ser vacíos por ahora
            rutas.add(ruta)
        }
    }

}
