package com.example.ridenshare.Logica

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.Data.Position
import com.example.ridenshare.Data.Ruta
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityCrearRutaDesdeMapaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class CrearRutaDesdeMapaActivity : AppCompatActivity() {
    lateinit var binding: ActivityCrearRutaDesdeMapaBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    lateinit var mapController: IMapController
    lateinit var roadManager: RoadManager
    var startPoint = GeoPoint(4.655764,-74.0981569)
    lateinit var lastPoint : GeoPoint
    lateinit var location: Location
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val routePoints = ArrayList<GeoPoint>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearRutaDesdeMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapController = binding.osmMap.controller

        Configuration.getInstance().setUserAgentValue(applicationContext.packageName)

        binding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.osmMap.setMultiTouchControls(true)

        mapController.setCenter(this.startPoint)
        mapController.setZoom(15.0)

        auth = Firebase.auth

        binding.osmMap.overlays.add(createOverlayEvents())

        roadManager = OSRMRoadManager(this, "ANDROID")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding.buttonCrear.setOnClickListener{
            if (binding.text.text.isEmpty()){
                Toast.makeText(this, "Es necesario asignar un titulo a la ruta", Toast.LENGTH_SHORT).show()
            }
            else if (routePoints.size < 2) {
                Toast.makeText(this, "Es necesario selecionar al menos dos puntos", Toast.LENGTH_SHORT).show()
            }
            else {
                crearRuta()
                finish()
            }
        }
    }

    fun convertGeoPointsToPositions(geoPoints: List<GeoPoint>): List<Position> {
        return geoPoints.map { geoPoint ->
            Position(
                latitud = geoPoint.latitude,
                longitud = geoPoint.longitude
            )
        }
    }

    private fun crearRuta() {
        // Get reference to Firebase
        myRef = database.getReference()
            .child("users")
            .child(auth.currentUser!!.uid)
            .child("rutas")
            .child(binding.text.text.toString())

        // Create an empty list for comments
        val emptyComments: List<String> = emptyList()

        // Create the Ruta object
        val ruta = Ruta(convertGeoPointsToPositions(routePoints), emptyComments)

        // Write the Ruta object to Firebase
        myRef.setValue(ruta)
            .addOnSuccessListener {
                println("Ruta added successfully!")
            }
            .addOnFailureListener { e ->
                println("Error adding ruta: ${e.message}")
            }
    }


    private var roadOverlay: Polyline? = null

    private fun drawRoute(finish: GeoPoint) {
        routePoints.add(finish)

        if(routePoints.isEmpty()){

        }
        else{
            val road = roadManager.getRoad(routePoints)

            if (binding.osmMap != null) {
                roadOverlay = RoadManager.buildRoadOverlay(road)
                roadOverlay?.outlinePaint?.color = Color.RED
                roadOverlay?.outlinePaint?.strokeWidth = 10f
                binding.osmMap.overlays.add(roadOverlay)

            }

        }

    }

    private fun createOverlayEvents(): MapEventsOverlay {
        val overlayEventos = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }
            override fun longPressHelper(p: GeoPoint): Boolean {
                longPressOnMap(p)
                return true
            }
        })
        return overlayEventos
    }

    private var longPressedMarker: Marker? = null
    private fun longPressOnMap(p: GeoPoint) {
        longPressedMarker = null
        longPressedMarker = crearMarcador(p)
        longPressedMarker?.let { binding.osmMap.overlays.add(it) }
//        Toast.makeText(this, "Long press hecho", Toast.LENGTH_SHORT).show()
        drawRoute(p)
    }

    private fun crearMarcador(p: GeoPoint): Marker?{
        var marker: Marker? = null
        if (binding.osmMap != null){
            marker = Marker(binding.osmMap)
            marker.icon = resources.getDrawable(R.drawable.baseline_adjust_24, this.theme)
            marker.position = p
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        return marker
    }
}