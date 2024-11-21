package com.example.ridenshare.Interfaz

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PhotoApi {
    @Multipart
    @POST("photos/upload")
    fun uploadPhoto(
        @Part file: MultipartBody.Part,
        @Header("Name") name: String
    ): Call<ResponseBody>
}