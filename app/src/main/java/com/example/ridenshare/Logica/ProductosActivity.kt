package com.example.ridenshare.Logica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Adapter.ProductoAdapter
import com.example.ridenshare.Adapter.PublicacionAdapter
import com.example.ridenshare.Data.Producto
import com.example.ridenshare.Data.Publicacion
import com.example.ridenshare.Interfaz.RetrofitInstance
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityProductosBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductosActivity : AppCompatActivity() {
    lateinit var binding: ActivityProductosBinding
    private val productos = mutableListOf<Producto>()
    private lateinit var adapter: ProductoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ProductoAdapter(this, productos)
        binding.lvProductos.adapter = adapter

        fetchPublicaciones()

    }

    private fun fetchPublicaciones() {
        val api = RetrofitInstance.apiProducto

        api.getProductos().enqueue(object : Callback<List<Producto>> {
            override fun onResponse(
                call: Call<List<Producto>>,
                response: Response<List<Producto>>
            ) {
                if (response.isSuccessful) {
                    val fetchedData = response.body()
                    if (fetchedData != null) {
                        productos.clear()
                        productos.addAll(fetchedData.reversed())
                        adapter.notifyDataSetChanged() // Update the ListView
                    }
                } else {
                    Toast.makeText(
                        this@ProductosActivity,
                        "Failed to fetch data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                Toast.makeText(
                    this@ProductosActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}