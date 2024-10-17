package com.example.ridenshare.Logica

import com.example.ridenshare.Data.Usuario
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.ridenshare.databinding.ActivityMainBinding
import android.content.Context
import org.json.JSONArray
import java.io.File
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetInputs()
        setupBiometricLogin()

        // Cambia el texto del botón según los campos de entrada
        binding.editCorreo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.editCorreo.text.isEmpty() && binding.editContrasenia.text.isEmpty()) {
                    binding.buttonIngresar.text = "Ingresar con huella"
                } else {
                    binding.buttonIngresar.text = "Ingresar"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Acción al presionar el botón "Ingresar"
        binding.buttonIngresar.setOnClickListener {
            if (binding.editCorreo.text.isEmpty()) {
                authenticateUser() // Autenticación biométrica
            } else {
                val usuarios = leerUsuariosDesdeJson() // Leer usuarios del archivo JSON
                val correoIngresado = binding.editCorreo.text.toString()
                val contraseñaIngresada = binding.editContrasenia.text.toString()
                val usuarioLogueado = usuarios.find { it.correo == correoIngresado && it.contraseña == contraseñaIngresada }
                if(usuarioLogueado != null){
                    val bundle = Bundle().apply {
                        putString("name", usuarioLogueado.nombre)
                        putString("surname", usuarioLogueado.apellido)
                        putString("city", usuarioLogueado.ciudadNacimiento)
                        putString("date", usuarioLogueado.fechaNacimiento)
                        putString("user", usuarioLogueado.correo)
                        putString("password", usuarioLogueado.contraseña)
                    }
                    startActivity(Intent(this, FeedActivity::class.java).putExtras(bundle))
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Enlace para registrarse
        binding.linkRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        resetInputs()
    }

    // Limpiar los campos de entrada
    private fun resetInputs() {
        binding.editCorreo.text.clear()
        binding.editContrasenia.text.clear()
    }

    // Configuración de autenticación biométrica
    private fun setupBiometricLogin() {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Login biométrico exitoso
                    proceedToFeedActivity()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login biométrico")
            .setSubtitle("Ingrese su huella para autenticarse")
            .setNegativeButtonText("Use sus credenciales normales")
            .build()
    }

    // Método para iniciar autenticación biométrica
    private fun authenticateUser() {
        biometricPrompt.authenticate(promptInfo)
    }

    // Navegar a FeedActivity después de autenticación biométrica
    private fun proceedToFeedActivity() {
        val bundle = Bundle().apply {
            putString("name", "name") // Datos ficticios, cambiar por datos reales
            putString("surname", "surname")
            putString("city", "city")
            putString("date", "date")
            putString("user", "user")
            putString("password", "password")
        }
        startActivity(Intent(this, FeedActivity::class.java).putExtras(bundle))
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

    // Verificar las credenciales de login
    private fun verificarLogin(usuarios: List<Usuario>, correo: String, contraseña: String): Boolean {
        return usuarios.any { it.correo == correo && it.contraseña == contraseña }
    }
}
