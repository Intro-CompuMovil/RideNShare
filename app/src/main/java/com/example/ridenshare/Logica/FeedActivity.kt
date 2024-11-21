package com.example.ridenshare.Logica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Log
import androidx.core.content.res.ResourcesCompat

import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ridenshare.Data.CustomTypefaceSpan
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityFeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val customTitle = TextView(this).apply {
            text = "Mi cuenta"
            textSize = 40f // Set text size
            typeface = ResourcesCompat.getFont(this@FeedActivity, R.font.londrina_solid_black) // Use custom font
            setTextColor(ContextCompat.getColor(this@FeedActivity, R.color.letras))
        }

        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER // Align title as needed
        }

        customTitle.layoutParams = layoutParams
        toolbar.addView(customTitle)

        binding.cardRutas.setOnClickListener{
            startActivity(Intent(this, TodasRutasActivity::class.java))
        }


        binding.buttonMisRutas.setOnClickListener{
            startActivity(Intent(this, MisRutasActivity::class.java));
        }

        binding.cardStore.setOnClickListener{
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        binding.cardSafetyTips.setOnClickListener{
            startActivity(Intent(this, TipsActivity::class.java))
        }

        binding.cardPublicaciones.setOnClickListener{
            startActivity(Intent(this, PublicacionesActivity::class.java))
        }

        binding.agregarButton.setOnClickListener{
            startActivity(Intent(this, CrearPublicacionActivity::class.java))
        }

        binding.MapButton.setOnClickListener{
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.crearRuta.setOnClickListener{
            startActivity(Intent(this, CrearRutaDesdeMapaActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Apply custom font to menu items
        val typeface = ResourcesCompat.getFont(this, R.font.londrina_solid_black)
        menu?.let {
            for (i in 0 until it.size()) {
                val menuItem = it.getItem(i)
                val spannableTitle = SpannableString(menuItem.title).apply {
                    setSpan(
                        CustomTypefaceSpan("", typeface!!), // Use the custom TypefaceSpan
                        0,
                        length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
                menuItem.title = spannableTitle
            }
        }
        return true
    }



    @SuppressLint("RestrictedApi")
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return super.onPrepareOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Log.d("DEBUGMIO", auth.uid.toString())
                auth.signOut()
                startActivity(Intent(this,MainActivity::class.java))
            }
            R.id.friends -> {
                startActivity(Intent(this, FriendsActivity::class.java))
            }
            R.id.cuenta -> {
                startActivity(Intent(this, PerfilActivity::class.java))
            }

        }
        return true
    }

    override fun onBackPressed() {
        // Close drawer on back press if it's open
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}