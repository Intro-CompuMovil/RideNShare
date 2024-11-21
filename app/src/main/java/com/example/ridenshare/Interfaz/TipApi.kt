package com.example.ridenshare.Interfaz

import com.example.ridenshare.Data.Tip
import retrofit2.Call
import retrofit2.http.GET

interface TipApi {
    @GET("/tip/all") // Replace with your actual endpoint
    fun getTips(): Call<List<Tip>>
}