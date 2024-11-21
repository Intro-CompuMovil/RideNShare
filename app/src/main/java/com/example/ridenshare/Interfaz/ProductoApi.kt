package com.example.ridenshare.Interfaz

import com.example.ridenshare.Data.Producto
import com.example.ridenshare.Data.Publicacion
import retrofit2.Call
import retrofit2.http.GET

interface ProductoApi {
    @GET("/producto/all") // Replace with your actual endpoint
    fun getProductos(): Call<List<Producto>>
}