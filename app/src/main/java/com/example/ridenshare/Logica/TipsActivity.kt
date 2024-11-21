package com.example.ridenshare.Logica

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Adapter.PublicacionAdapter
import com.example.ridenshare.Adapter.TipAdapter
import com.example.ridenshare.Data.Publicacion
import com.example.ridenshare.Data.Tip
import com.example.ridenshare.Interfaz.RetrofitInstance
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityTipsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TipsActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipsBinding
    private val tips = mutableListOf<Tip>()
    private lateinit var adapter: TipAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TipAdapter(this, tips)
        binding.lvPublicaciones.adapter = adapter

        fetchPublicaciones()
    }

    private fun fetchPublicaciones() {
        val api = RetrofitInstance.apiTip

        api.getTips().enqueue(object : Callback<List<Tip>> {
            override fun onResponse(
                call: Call<List<Tip>>,
                response: Response<List<Tip>>
            ) {
                if (response.isSuccessful) {
                    val fetchedData = response.body()
                    Log.i("DEBUGMIO", "fetchedData.toString()")
                    if (fetchedData != null) {
                        tips.clear()
                        tips.addAll(fetchedData.reversed())
                        adapter.notifyDataSetChanged() // Update the ListView
                    }
                } else {
                    Toast.makeText(
                        this@TipsActivity,
                        "Failed to fetch data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Tip>>, t: Throwable) {
                Toast.makeText(
                    this@TipsActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}