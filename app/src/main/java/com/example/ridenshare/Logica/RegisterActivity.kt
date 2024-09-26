package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetInputs()

        binding.buttonRegistrar.setOnClickListener{
            if (validarDatos()){
                val bundle = Bundle()
                bundle.putString("name",binding.editNombre.text.toString())
                bundle.putString("surname",binding.editApellido.text.toString())
                bundle.putString("date",binding.editFecha.text.toString())
                bundle.putString("city",binding.editCiudad.text.toString())
                bundle.putString("user", binding.editCorreo.text.toString())
                bundle.putString("password", binding.editContrasenia.text.toString())
                val intent = Intent(this, FeedActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
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
        if (binding.editCorreo.text.toString().matches(Regex(emailRegex))){
            if (binding.editContrasenia.text.toString().length >= 8){
                return true
            }
            Toast.makeText(this, "La contraseña ingresada es muy corta", Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(this, "El correo ingresado no es válido", Toast.LENGTH_SHORT).show()
        return false
    }
}