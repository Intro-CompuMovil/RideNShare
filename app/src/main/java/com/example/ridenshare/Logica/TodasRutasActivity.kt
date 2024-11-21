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

class TodasRutasActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todas_rutas)

        listView = findViewById(R.id.rutasListView)
        database = FirebaseDatabase.getInstance()
        auth = Firebase.auth

        // Reference to all users in Firebase
        val usersRef = database.getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Initialize a list to hold all ruta names from all users
                val allRutaNames = mutableListOf<String>()

                // Iterate over all users
                for (userSnapshot in snapshot.children) {
                    // Check if the user has "rutas"
                    val rutasSnapshot = userSnapshot.child("rutas")
                    for (rutaSnapshot in rutasSnapshot.children) {
                        // Collect Ruta names (keys)
                        val rutaName = rutaSnapshot.key ?: ""
                        if (rutaName.isNotEmpty()) {
                            allRutaNames.add(rutaName)
                        }
                    }
                }

                // Create and set the adapter with all ruta names
                val adapter = RutaAdapter(
                    context = this@TodasRutasActivity,
                    rutaList = allRutaNames,
                    onCommentClickListener = { rutaName ->
                        // Handle viewing comments for the selected Ruta
                        showComments(rutaName)
                    },
                    onAddComment = { rutaName, comment ->
                        // Reference to the Firebase location for comments
                        val rutaRef = FirebaseDatabase.getInstance()
                            .getReference("users").child(auth.currentUser!!.uid).child("rutas").child(rutaName).child("comentarios")

                        // Create a map with currentUser.uid as the key and comment as the value
                        val commentMap = mapOf(auth.currentUser!!.uid to comment)

                        // Push the map to Firebase
                        rutaRef.push().setValue(commentMap)
                            .addOnSuccessListener {
                                Toast.makeText(this@TodasRutasActivity, "Comment added to $rutaName", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@TodasRutasActivity, "Failed to add comment: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                )

                listView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@TodasRutasActivity,
                    "Error loading routes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showComments(rutaName: String) {
        // Reference to the Firebase location for comments
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
                AlertDialog.Builder(this@TodasRutasActivity)
                    .setTitle("Comentarios de la ruta $rutaName")
                    .setMessage(commentsText)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@TodasRutasActivity,
                    "Error cargando los comentarios",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
