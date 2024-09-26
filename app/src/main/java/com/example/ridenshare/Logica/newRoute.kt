package com.example.ridenshare.Logica

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.R

class newRoute : AppCompatActivity() {

    private lateinit var imageViewFoto: ImageView
    private lateinit var editTextDescripcion: EditText
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_route)

        val permissionGranted = intent.getBooleanExtra("permissionGranted", false)
        if (!permissionGranted) {
            Toast.makeText(this, "Funcionalidades reducidas", Toast.LENGTH_SHORT).show()
        }
        else{
            imageViewFoto = findViewById(R.id.imageViewFoto)
            editTextDescripcion = findViewById(R.id.editTextDescripcion)

            // Botón para tomar la foto
            val tomarFotoButton = findViewById<Button>(R.id.tomarFotoButton)
            tomarFotoButton.setOnClickListener {
                abrirCamara()
            }

            val publicarButton = findViewById<Button>(R.id.publicarButton)
            publicarButton.setOnClickListener {
                val descripcion = editTextDescripcion.text.toString()
                if (descripcion.isNotEmpty()) {
                    // Lógica para publicar la imagen y la descripción
                    Toast.makeText(this, "Publicando: $descripcion", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Ingresa una descripción", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun abrirCamara() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageViewFoto.setImageBitmap(imageBitmap) // Mostrar la imagen capturada
        }
    }
}