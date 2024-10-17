package com.example.ridenshare.Logica

import com.example.ridenshare.Data.Usuario
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.databinding.ActivityRegisterBinding
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileWriter
import java.io.Writer

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetInputs()

        binding.buttonRegistrar.setOnClickListener {
            if (validarDatos()) {
                // Verifica si el usuario ya está registrado
                val usuarios = leerUsuariosDesdeJson()

                val correoIngresado = binding.editCorreo.text.toString()
                if (usuarios.any { it.correo == correoIngresado }) {
                    // El correo ya está en uso
                    Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                } else {
                    // Si no está registrado, se agrega el nuevo usuario
                    val nuevoUsuario = Usuario(
                        binding.editNombre.text.toString(),
                        binding.editApellido.text.toString(),
                        binding.editCiudad.text.toString(),
                        binding.editFecha.text.toString(),
                        correoIngresado,
                        binding.editContrasenia.text.toString()
                    )
                    usuarios.add(nuevoUsuario)
                    escribirUsuariosEnJson(usuarios)
                    val bundle = Bundle()
                    bundle.putString("name", nuevoUsuario.nombre)
                    bundle.putString("surname", nuevoUsuario.apellido)
                    bundle.putString("city", nuevoUsuario.ciudadNacimiento)
                    bundle.putString("date", nuevoUsuario.fechaNacimiento)
                    bundle.putString("user", nuevoUsuario.correo)
                    bundle.putString("password", nuevoUsuario.contraseña)
                    // Ir a la siguiente actividad (FeedActivity)
                    val intent = Intent(this, FeedActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetInputs()
    }

    private fun resetInputs() {
        binding.editCorreo.setText("")
        binding.editFecha.setText("")
        binding.editNombre.setText("")
        binding.editCiudad.setText("")
        binding.editApellido.setText("")
        binding.editContrasenia.setText("")
    }

    private fun validarDatos(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        if (binding.editCorreo.text.toString().matches(Regex(emailRegex))) {
            if (binding.editContrasenia.text.toString().length >= 8) {
                return true
            }
            Toast.makeText(this, "La contraseña ingresada es muy corta", Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(this, "El correo ingresado no es válido", Toast.LENGTH_SHORT).show()
        return false
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
