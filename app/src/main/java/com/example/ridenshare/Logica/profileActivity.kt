package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.R

class profileActivity : AppCompatActivity() {
    private lateinit var editNombre: EditText
    private lateinit var editApellido: EditText
    private lateinit var editCiudad: EditText
    private lateinit var editFechaNacimiento: EditText
    private lateinit var editCorreo: EditText
    private lateinit var editContrasenia: EditText
    private lateinit var buttonGuardar: Button
    private lateinit var buttonRutas: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        editNombre = findViewById(R.id.editNombre)
        editApellido = findViewById(R.id.editApellido)
        editCiudad = findViewById(R.id.editCiudad)
        editFechaNacimiento = findViewById(R.id.editFechaNacimiento)
        editCorreo = findViewById(R.id.editCorreo)
        editContrasenia = findViewById(R.id.editContrasenia)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonRutas = findViewById(R.id.buttonRutas)

        // Obtener datos del Intent
        val extras = intent.extras
        extras?.let {
            editNombre.setText(it.getString("name"))
            editApellido.setText(it.getString("surname"))
            editCiudad.setText(it.getString("city"))
            editFechaNacimiento.setText(it.getString("date"))
            editCorreo.setText(it.getString("user"))
            editContrasenia.setText(it.getString("password"))
            // Puedes asignar los valores de usuario y contraseña si es necesario
        }
        buttonGuardar.setOnClickListener {
            guardarCambios()
        }

        buttonRutas.setOnClickListener{
            val intent = Intent(this, myRoutes::class.java)
            startActivity(intent)
        }


    }
    private fun guardarCambios() {
        // Aquí puedes obtener los valores de los campos
        val name = editNombre.text.toString()
        val surname = editApellido.text.toString()
        val city = editCiudad.text.toString()
        val date = editFechaNacimiento.text.toString()
        val user = editCorreo.text.toString()
        val password = editContrasenia.text.toString()

        val extras = intent.extras

        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("surname", surname)
        bundle.putString("city", city)
        bundle.putString("date", date)
        bundle.putString("user", user)
        bundle.putString("password", password)
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish() // Opcional, para cerrar la actividad actual
    }
}