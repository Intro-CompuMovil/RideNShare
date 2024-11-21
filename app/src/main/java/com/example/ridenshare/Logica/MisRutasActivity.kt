package com.example.ridenshare.Logica

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Adapter.RutaAdapter
import com.example.ridenshare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MisRutasActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_rutas)

        listView = findViewById(R.id.rutasListView)
        database = FirebaseDatabase.getInstance()

        auth = Firebase.auth

        // Reference to the Rutas in Firebase
        val rutasRef = database.getReference("users").child(auth.currentUser!!.uid).child("rutas")

        rutasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Collect Ruta names/keys
                val rutaNames = snapshot.children.map { it.key ?: "" }

                // Create and set the adapter
                val adapter = RutaAdapter(
                    context = this@MisRutasActivity,
                    rutaList = rutaNames,
                    onCommentClickListener = { rutaName ->
                        // Handle viewing comments for the selected Ruta
                        showComments(rutaName)
                    },
                    onAddComment = { rutaName, comment ->
                        // Reference to the Firebase location for comments
                        val rutaRef = FirebaseDatabase.getInstance()
                            .getReference("users/${auth.currentUser!!.uid}/rutas/$rutaName/comentarios")

                        // Create a map with currentUser.uid as the key and comment as the value
                        val commentMap = mapOf(auth.currentUser!!.uid to comment)

                        // Push the map to Firebase
                        rutaRef.push().setValue(commentMap)
                            .addOnSuccessListener {
                                Toast.makeText(this@MisRutasActivity, "Comment added to $rutaName", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@MisRutasActivity, "Failed to add comment: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                )

                listView.adapter = adapter


                listView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MisRutasActivity,
                    "Error loading routes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showComments(rutaName: String) {
        // Implement logic to show comments for the selected Ruta
//        val commentsRef = database.getReference("/users/${auth.currentUser!!.uid}/rutas/$rutaName/comentarios")
        val commentsRef = database.getReference("users").child(auth.currentUser!!.uid).child("rutas").child(rutaName).child("comentarios")
        commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("DEBUGMIO", snapshot.toString())

                // Define the expected data structure as Map<String, String>
                val comments = snapshot.children
                    .mapNotNull { it.getValue(object : GenericTypeIndicator<Map<String, String>>() {}) }
                    .flatMap { it.values }  // Extract only the values (the comments)

                // Example: Show comments in an AlertDialog
                val commentsText = comments.joinToString("\n")
                AlertDialog.Builder(this@MisRutasActivity)
                    .setTitle("Comentarios de la ruta $rutaName")
                    .setMessage(commentsText)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }



            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MisRutasActivity,
                    "Error cargando los comentarios",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}