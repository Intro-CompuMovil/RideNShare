package com.example.ridenshare.Logica

import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Adapter.PublicacionAdapter
import com.example.ridenshare.Data.Publicacion
import com.example.ridenshare.Interfaz.RetrofitInstance
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityPublicacionesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublicacionesActivity : AppCompatActivity() {
    lateinit var binding: ActivityPublicacionesBinding
    private val publicaciones = mutableListOf<Publicacion>()
    private lateinit var adapter: PublicacionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublicacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PublicacionAdapter(this, publicaciones)
        binding.lvPublicaciones.adapter = adapter

        fetchPublicaciones()
    }

    private fun fetchPublicaciones() {
        val api = RetrofitInstance.apiPublicacion

        api.getPublicaciones().enqueue(object : Callback<List<Publicacion>> {
            override fun onResponse(
                call: Call<List<Publicacion>>,
                response: Response<List<Publicacion>>
            ) {
                if (response.isSuccessful) {
                    val fetchedData = response.body()
                    if (fetchedData != null) {
                        publicaciones.clear()
                        publicaciones.addAll(fetchedData.reversed())
                        adapter.notifyDataSetChanged() // Update the ListView
                    }
                } else {
                    Toast.makeText(
                        this@PublicacionesActivity,
                        "Failed to fetch data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Publicacion>>, t: Throwable) {
                Toast.makeText(
                    this@PublicacionesActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}