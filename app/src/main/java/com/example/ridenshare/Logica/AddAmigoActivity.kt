package com.example.ridenshare.Logica

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityAddAmigoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class AddAmigoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddAmigoBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAmigoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.buttonAdd.setOnClickListener{
            checkIfFriendByEmail(binding.editCorreo.text.toString()) { isFriend ->
//                if (isFriend) {
//                    Toast.makeText(this, "This user is your friend!", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "This user is not your friend.", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

    fun checkIfFriendByEmail(friendEmail: String, onResult: (Boolean) -> Unit) {
        // Get the current user's ID from Firebase Authentication
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            onResult(false)
            return
        }

        // Get reference to the current user's friends in the database
        val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(currentUserId).child("Friends")

        // Query the users node to find a user by email
        val usersRef = FirebaseDatabase.getInstance().reference.child("users")

        // Look for the user by email
        usersRef.orderByChild("correo").equalTo(friendEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // If a user with this email exists, check if they are a friend
                    val friendUserId = snapshot.children.firstOrNull()?.key // Get the user's ID

                    // Check if the user is in the current user's friends list
                    if (friendUserId != null) {
                        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(friendSnapshot: DataSnapshot) {
                                // Check if the friendUserId exists in the friends list
                                val isFriend = friendSnapshot.children.any { it.value == friendUserId }
                                onResult(isFriend)

                                // Show corresponding Toast based on the result
                                if (isFriend) {
                                    Toast.makeText(this@AddAmigoActivity, "Este usuario ya es tu amigo", Toast.LENGTH_SHORT).show()
                                } else {
//                                    Toast.makeText(this@AddAmigoActivity, "This user is not your friend.", Toast.LENGTH_SHORT).show()
                                    addFriend(friendEmail, currentUserId, this@AddAmigoActivity)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                onResult(false)
                            }
                        })
                    } else {
                        onResult(false)
//                        Toast.makeText(this@AddAmigoActivity, "This user is not your friend.", Toast.LENGTH_SHORT).show()
                        addFriend(friendEmail, currentUserId, this@AddAmigoActivity)
                    }
                } else {
                    // No user found with this email
                    onResult(false)
                    Toast.makeText(this@AddAmigoActivity, "No existe un usario con este correo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }

    fun addFriend(friendEmail: String, currentUserId: String, context: Context) {
        // Query the users node to find the user by email again
        val usersRef = FirebaseDatabase.getInstance().reference.child("users")
        usersRef.orderByChild("correo").equalTo(friendEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Get the user ID for the found email
                    val friendUserId = snapshot.children.firstOrNull()?.key

                    if (friendUserId != null) {
                        // Generate a unique alias for this friend (e.g., "burntAlias_1234")
                        val alias = "burntAlias_${System.currentTimeMillis()}"

                        // Add the friend to the current user's friends list
                        val currentUserFriendsRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserId).child("Friends")
                        currentUserFriendsRef.child(binding.editAlias.text.toString()).setValue(friendUserId).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // If adding the friend was successful, show a success Toast
                                Toast.makeText(context, "Amigo añadido exitosamente!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@AddAmigoActivity, FriendsActivity::class.java))
                            } else {
                                // If something went wrong, show an error Toast
                                Toast.makeText(context, "Error añadiendo amigo.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@AddAmigoActivity, FriendsActivity::class.java))
                            }
                        }
                    }
                } else {
                    // If no user was found with this email, show an error message
                    Toast.makeText(context, "No se encontró un usario con este correo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error adding friend.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}