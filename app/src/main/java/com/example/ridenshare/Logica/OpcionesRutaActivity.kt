package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityOpcionesRutaBinding

class OpcionesRutaActivity : AppCompatActivity() {
    lateinit var binding: ActivityOpcionesRutaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesRutaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonDesdeMapa.setOnClickListener{
            startActivity(Intent(this, CrearRutaDesdeMapaActivity::class.java))
        }

        binding.buttonTiempoReal.setOnClickListener{
           startActivity(Intent(this, CrearRutaTiempoRealActivity::class.java))
        }
    }
}