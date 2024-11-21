package com.example.ridenshare.Interfaz

import com.example.ridenshare.Data.Publicacion
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PublicacionApi {
    @GET("/publicacion/all") // Replace with your actual endpoint
    fun getPublicaciones(): Call<List<Publicacion>>

    @POST("/publicacion")
    fun createPublicacion(@Body publicacion: Publicacion): Call<Int>

}
