package com.example.ridenshare.Logica

import android.content.Intent
import androidx.biometric.BiometricPrompt
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ridenshare.Data.Data
import com.example.ridenshare.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

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
                signInUser(binding.editCorreo.text.toString(), binding.editContrasenia.text.toString());
            }
        }

        // Enlace para registrarse
        binding.linkRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInUser(email: String, password: String){
        if(isEmailValid(email)){
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
// Sign in success, update UI
//                        Log.d(TAG, "signInWithEmail:success:")
                        val user = auth.currentUser
                        Data.default_user = email
                        Data.default_password = password
                        updateUI(auth.currentUser)
                    } else {
//                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }


    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@") ||
            !email.contains(".") ||
            email.length < 5)
            return false
        return true
    }


//    private fun validateForm(): Boolean {
//        var valid = true
//        val email = binding.editCorreo.text.toString()
//        if (TextUtils.isEmpty(email)) {
//            binding.editCorreo.error = "Required."
//            valid = false
//        } else {
//            binding.editCorreo.error = null
//        }
//        val password = binding.editContrasenia.text.toString()
//        if (TextUtils.isEmpty(password)) {
//            binding.editContrasenia.error = "Required."
//            valid = false
//        } else {
//            binding.editContrasenia.error = null
//        }
//        return valid
//    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            proceedToFeedActivity()
        } else {

        }
    }


    private fun authenticateUser() {
        biometricPrompt.authenticate(promptInfo)
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

    private fun setupBiometricLogin() {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Sign in default user
                    signInUser(Data.default_user, Data.default_password);
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

    private fun proceedToFeedActivity() {
        startActivity(Intent(this, FeedActivity::class.java))
    }
}