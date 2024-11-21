package com.example.ridenshare.Data

import org.osmdroid.util.GeoPoint

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Ruta(
    val positions: List<Position> = emptyList(),
    val comentarios: List<String> = emptyList() // Use the exact name for Firebase compatibility
)
