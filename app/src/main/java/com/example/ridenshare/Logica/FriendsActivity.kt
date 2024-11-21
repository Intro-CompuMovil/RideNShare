package com.example.ridenshare.Logica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Adapter.MyUserAdapter
import com.example.ridenshare.Data.MyUser
import com.example.ridenshare.Data.Position
import com.example.ridenshare.Data.UserAdapter
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class FriendsActivity : AppCompatActivity() {
    lateinit var binding: ActivityFriendsBinding;
    private lateinit var adapter: MyUserAdapter
    private val userList = mutableListOf<UserAdapter>()
    private val friendList = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        myRef = database.getReference().child("users").child(auth.currentUser!!.uid).child("Friends")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendList.clear()
                for (friendSnapshot in snapshot.children) {
                    val friendId = friendSnapshot.value.toString()
                    friendList.add(friendId)
                }
                populateListView(friendList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch friends: ${error.message}")
            }
        })

        binding.buttonAdd.setOnClickListener{
            startActivity(Intent(this, AddAmigoActivity::class.java))
        }

        binding.lvAmigos.setOnItemClickListener{ _, _, position, _ ->

            val selectedUser = userList[position]

            val intent = Intent(this, GoToAmigoActivity::class.java)
            val bundle = Bundle()
            bundle.putString("correo", selectedUser.correo)
            intent.putExtras(bundle)
            startActivity(intent)

        }

//        binding.lvAmigos.setOnItemLongClickListener{_, _, position, _ ->
//
//            val selectedUser = userList[position]
//            Toast.makeText(this, selectedUser.correo, Toast.LENGTH_SHORT).show()
//        }
    }

    private fun populateListView(friendList: MutableList<String>) {
        userList.clear()
        friendList.forEach{friendId ->
            val friendLocationRef = database.getReference("users").child(friendId)
            friendLocationRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(MyUser::class.java)
                    if (user != null) {
                        val userA = UserAdapter()
                        userA.apodo = user.nombre + " " + user.apellido
                        userA.ciudad = user.ciudad
                        userA.correo = user.correo
                        userList.add(userA)
                    }
                    adapter = MyUserAdapter(this@FriendsActivity, userList)
                    binding.lvAmigos.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching users for $friendId: ${error.message}")
                }
            })

        }

    }
}