package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.ridenshare.databinding.ActivityMainBinding

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

        binding.editCorreo.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.editCorreo.text.toString() == "" && binding.editContrasenia.text.toString() == ""){
                    binding.buttonIngresar.setText("Ingresar con huella")
                }
                else {
                    binding.buttonIngresar.setText("Ingresar")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }
        })

        binding.buttonIngresar.setOnClickListener{

            val extras = intent.extras
            val user = extras?.getString("user") // Obtener el usuario
            val password = extras?.getString("password") // Obtener la contraseña
            val name = extras?.getString("name")
            val surname = extras?.getString("surname")
            val city = extras?.getString("city")
            val date = extras?.getString("date")

            if (binding.editCorreo.text.toString() == ""){
                authenticateUser()
            }
            else if ((binding.editCorreo.text.toString() == "user" && binding.editContrasenia.text.toString() == "password")){
                val bundle = Bundle()
                bundle.putString("name","Oscar")
                bundle.putString("surname","Martinez")
                bundle.putString("city","Bogota")
                bundle.putString("date","01/01/2000")
                bundle.putString("user", "user")
                bundle.putString("password", "password")
                val intent = Intent(this, FeedActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            else if (((binding.editCorreo.text.toString() == user && binding.editContrasenia.text.toString() == password))){
                val bundle = Bundle()
                bundle.putString("name",name)
                bundle.putString("surname",surname)
                bundle.putString("city",city)
                bundle.putString("date",date)
                bundle.putString("user", user)
                bundle.putString("password", password)
                val intent = Intent(this, FeedActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.linkRegistrarse.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        resetInputs()
    }

    private fun resetInputs() {
        binding.editCorreo.setText("")
        binding.editContrasenia.setText("")
    }

    private fun setupBiometricLogin() {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(this@MainActivity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(this@MainActivity, "Authentication succeeded!", Toast.LENGTH_SHORT).show()

                    // Proceed with login success actions (e.g., navigate to main activity)
                    proceedToMainActivity()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
//                    Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login biométrico")
            .setSubtitle("Ingrese su huella para autenticarse")
            .setNegativeButtonText("Use sus credenciales normales")
            .build()
    }

    private fun authenticateUser() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun proceedToMainActivity() {
        val bundle = Bundle()
        bundle.putString("name","name")//TODO: Change so that uses the first stored user in the DB (JSON)
        bundle.putString("surname","surname")//TODO: Change so that uses the first stored user in the DB (JSON)
        bundle.putString("city","city")//TODO: Change so that uses the first stored user in the DB (JSON)
        bundle.putString("date","date")//TODO: Change so that uses the first stored user in the DB (JSON)
        bundle.putString("user", "user")//TODO: Change so that uses the first stored user in the DB (JSON)
        bundle.putString("password", "password")//TODO: Change so that uses the first stored user in the DB (JSON)
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}