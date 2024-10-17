package com.example.ridenshare.Logica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.databinding.ActivityMyRoutesBinding

class myRoutes : AppCompatActivity() {
    private lateinit var binding: ActivityMyRoutesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}