package com.example.ridenshare.Data

class Position {
    var latitud: Double? = null
    var longitud: Double? = null

    // No-argument constructor
    constructor() {
        // You can initialize the properties with default values if needed
        this.latitud = null
        this.longitud = null
    }

    // Existing constructor
    constructor(latitud: Double?, longitud: Double?) {
        this.latitud = latitud
        this.longitud = longitud
    }
}
