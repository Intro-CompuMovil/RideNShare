package com.example.ridenshare.Logica

import com.google.android.gms.maps.model.LatLng

data class Ruta(
    val nombre: String,
    val distancia: Double,
    val tiempo: String,
    val puntos: List<LatLng>
)
