package com.example.ridenshare.Logica

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Data.Data
import com.example.ridenshare.Data.MyUser
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DatabaseReference

class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        resetInputs()

        binding.buttonRegistrar.setOnClickListener{
            if (validarDatos()) {
                crearUsuario(binding.editCorreo.text.toString(), binding.editContrasenia.text.toString()    )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetInputs()
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

    private fun resetInputs() {
        binding.editCorreo.setText("")
        binding.editFecha.setText("")
        binding.editNombre.setText("")
        binding.editCiudad.setText("")
        binding.editApellido.setText("")
        binding.editContrasenia.setText("")
    }

    fun crearUsuario(email: String, password: String){
        Log.i(ContentValues.TAG, email)
        this.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
                    val user = auth.currentUser
                    if (user != null) {
                        val upcrb = UserProfileChangeRequest.Builder()
                        upcrb.setDisplayName(binding.editNombre.text.toString() + " " + binding.editApellido.text.toString())
                        upcrb.setPhotoUri(Uri.parse("path/to/pic")) //fake uri, use Firebase Storage
                        user.updateProfile(upcrb.build())

                        val myUser = MyUser()
                        myUser.nombre = binding.editNombre.text.toString()
                        myUser.apellido = binding.editApellido.text.toString()
                        myUser.correo = binding.editCorreo.text.toString()
                        myUser.fecha_nacimiento = binding.editFecha.text.toString()
                        myUser.ciudad = binding.editCiudad.text.toString()
//                        myUser.password = binding.editContrasenia.text.toString()
                        myUser.posicion.latitud = 0.0
                        myUser.posicion.longitud = 0.0

                        myRef = database.getReference(Data.PATH_USERS+auth.currentUser!!.uid)
                        myRef.setValue(myUser)

                        Data.default_user = binding.editCorreo.text.toString()
                        Data.default_password = binding.editContrasenia.text.toString()

                        startActivity(Intent(this, FeedActivity::class.java))
                    }
                } else {
                    Toast.makeText(this, "createUserWithEmail:Failure: " + task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    task.exception?.message?.let { Log.e(ContentValues.TAG, it) }
                }
            }

    }
}