package com.example.ridenshare.Logica

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ridenshare.R

class FeedActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val intent = Intent(this, newRoute::class.java)
        intent.putExtra("permissionGranted", isGranted)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val post = findViewById<ImageButton>(R.id.agregarButton)
        val back = findViewById<ImageButton>(R.id.backButton)
        val map = findViewById<ImageButton>(R.id.MapButton)
        val profile = findViewById<ImageButton>(R.id.profileButton)
        val newRoute = findViewById<Button>(R.id.crearRuta)

        post.setOnClickListener {
            requestCamaraPermission()
        }

        back.setOnClickListener {
            val extras = intent.extras
            if (extras != null) {
                val user = extras.getString("user") // Obtener el usuario
                val password = extras.getString("password") // Obtener la contraseña
                val name = extras.getString("name")
                val surname = extras.getString("surname")
                val city = extras.getString("city")
                val date = extras.getString("date")
                val bundle = Bundle()
                bundle.putString("name", name)
                bundle.putString("surname", surname)
                bundle.putString("city", city)
                bundle.putString("date", date)
                bundle.putString("user", user)
                bundle.putString("password", password)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        map.setOnClickListener {
            val intent = Intent(this, mapActivity::class.java)
            startActivity(intent)
        }

        profile.setOnClickListener {
            val extras = intent.extras
            if (extras != null) {
                val user = extras.getString("user") // Obtener el usuario
                val password = extras.getString("password") // Obtener la contraseña
                val name = extras.getString("name")
                val surname = extras.getString("surname")
                val city = extras.getString("city")
                val date = extras.getString("date")
                val bundle = Bundle()
                bundle.putString("name", name)
                bundle.putString("surname", surname)
                bundle.putString("city", city)
                bundle.putString("date", date)
                bundle.putString("user", user)
                bundle.putString("password", password)
                val intent = Intent(this, profileActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        newRoute.setOnClickListener{
            startActivity(Intent(this, CrearRutaActivity::class.java))
        }
    }

    private fun requestCamaraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                val intent = Intent(this, newRoute::class.java)
                intent.putExtra("permissionGranted", true)
                startActivity(intent)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}