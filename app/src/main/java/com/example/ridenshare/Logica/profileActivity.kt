package com.example.ridenshare.Logica

import com.example.ridenshare.Data.Usuario
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.R
import org.json.JSONArray
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

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
        }

        buttonGuardar.setOnClickListener {
            guardarCambios()
        }

        buttonRutas.setOnClickListener {
            val intent = Intent(this, myRoutes::class.java)
            startActivity(intent)
        }
    }

    private fun guardarCambios() {
        // Obtener los valores de los campos
        val name = editNombre.text.toString()
        val surname = editApellido.text.toString()
        val city = editCiudad.text.toString()
        val date = editFechaNacimiento.text.toString()
        val user = editCorreo.text.toString()
        val password = editContrasenia.text.toString()

        // Cargar usuarios desde el archivo JSON
        val usuarios = leerUsuariosDesdeJson()
        val usuarioLogueado = usuarios.find { it.correo == user }

        if (usuarioLogueado != null) {
            // Actualizar los datos del usuario
            usuarioLogueado.nombre = name
            usuarioLogueado.apellido = surname
            usuarioLogueado.ciudadNacimiento = city
            usuarioLogueado.fechaNacimiento = date
            usuarioLogueado.contraseña = password

            // Guardar cambios en el archivo JSON
            escribirUsuariosEnJson(usuarios)

            // Ir a FeedActivity con los datos actualizados
            val bundle = Bundle().apply {
                putString("name", name)
                putString("surname", surname)
                putString("city", city)
                putString("date", date)
                putString("user", user)
                putString("password", password)
            }
            val intent = Intent(this, FeedActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            finish() // Opcional, para cerrar la actividad actual
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun leerUsuariosDesdeJson(): MutableList<Usuario> {
        val usuariosList = mutableListOf<Usuario>()
        val file = File(baseContext.getExternalFilesDir(null), "usuarios.json")

        if (file.exists()) {
            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val usuario = Usuario(
                    jsonObject.getString("nombre"),
                    jsonObject.getString("apellido"),
                    jsonObject.getString("ciudad_nacimiento"),
                    jsonObject.getString("fecha_nacimiento"),
                    jsonObject.getString("correo"),
                    jsonObject.getString("contraseña")
                )
                usuariosList.add(usuario)
            }
        }
        return usuariosList
    }

    private fun escribirUsuariosEnJson(usuarios: List<Usuario>) {
        val jsonArray = JSONArray()
        usuarios.forEach { usuario ->
            jsonArray.put(usuario.toJSON())
        }

        val filename = "usuarios.json"
        try {
            val file = File(baseContext.getExternalFilesDir(null), filename)
            val output = BufferedWriter(FileWriter(file))
            output.write(jsonArray.toString())  // Escribe el arreglo completo
            output.close()
            Toast.makeText(applicationContext, "Usuario guardado", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error al guardar usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
