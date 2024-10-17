package com.example.ridenshare.Data

import org.json.JSONException
import org.json.JSONObject

data class Usuario(
    var nombre: String,
    var apellido: String,
    var ciudadNacimiento: String,
    var fechaNacimiento: String,  // Cambiado a String para el formato "dd/MM/yyyy"
    var correo: String,
    var contraseña: String
) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        try {
            obj.put("nombre", nombre)
            obj.put("apellido", apellido)
            obj.put("ciudad_nacimiento", ciudadNacimiento)
            obj.put("fecha_nacimiento", fechaNacimiento)
            obj.put("correo", correo)
            obj.put("contraseña", contraseña)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }
}