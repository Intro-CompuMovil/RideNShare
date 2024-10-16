package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.databinding.ActivityCrearRutaBinding

class CrearRutaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrearRutaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCrearRutaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonDesdeMapa.setOnClickListener{
            startActivity(Intent(this, RutaDesdeMapaActivity::class.java))
        }

        binding.buttonTiempoReal.setOnClickListener{
            startActivity(Intent(this, RutaTiempoRealActivity::class.java))
        }

    }
}