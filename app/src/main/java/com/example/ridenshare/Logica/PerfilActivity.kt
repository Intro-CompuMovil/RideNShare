package com.example.ridenshare.Logica

import android.content.Intent
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
import com.example.ridenshare.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.lang.Double

class PerfilActivity : AppCompatActivity() {
    lateinit var binding: ActivityPerfilBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        getAndSetDatos()

        binding.buttonGuardar.setOnClickListener{
            guardarCambios()
        }

    }

    private fun getAndSetDatos() {
        myRef = database.getReference(Data.PATH_USERS + auth.uid)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.editNombre.setText(dataSnapshot.child("nombre").value.toString())
                binding.editApellido.setText(dataSnapshot.child("apellido").value.toString())
                binding.editCiudad.setText(dataSnapshot.child("ciudad").value.toString())
                binding.editFechaNacimiento.setText(dataSnapshot.child("fecha_nacimiento").value.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun guardarCambios() {
        myRef = database.getReference(Data.PATH_USERS + auth.uid)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val myUser = MyUser()
                myUser.correo = dataSnapshot.child("correo").value.toString()
                myUser.posicion.latitud = dataSnapshot.child("latitud").value.toString().toDouble()
                myUser.posicion.longitud = dataSnapshot.child("longitud").value.toString().toDouble()
                myUser.apellido = binding.editApellido.text.toString()
                myUser.ciudad = binding.editCiudad.text.toString()
                myUser.nombre = binding.editNombre.text.toString()
                myUser.fecha_nacimiento = binding.editFechaNacimiento.text.toString()

                myRef = database.getReference(Data.PATH_USERS+auth.currentUser!!.uid)
                myRef.setValue(myUser)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}