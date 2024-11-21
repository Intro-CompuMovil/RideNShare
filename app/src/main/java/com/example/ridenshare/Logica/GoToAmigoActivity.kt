package com.example.ridenshare.Logica

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridenshare.Data.Data
import com.example.ridenshare.Data.Position
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityGoToAmigoBinding
import com.example.ridenshare.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.math.roundToInt

class GoToAmigoActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoToAmigoBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    lateinit var mapController: IMapController
    lateinit var roadManager: RoadManager
    val latitude = 4.6709
    val longitude = -74.2973
    val startPoint = GeoPoint(latitude, longitude)
    val lastPoint = GeoPoint(latitude, longitude)
    lateinit var location: Location
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val friendsList = mutableListOf<String>()
    private var friendPosition: GeoPoint = GeoPoint(latitude, longitude)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoToAmigoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapController = binding.osmMap.controller

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationRequest = createLocationRequest()

        Configuration.getInstance().setUserAgentValue(applicationContext.packageName)

        binding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.osmMap.setMultiTouchControls(true)

        mapController.setCenter(this.startPoint)
        mapController.setZoom(10.0)

        auth = Firebase.auth

//        binding.osmMap.overlays.add(createOverlayEvents())

        roadManager = OSRMRoadManager(this, "ANDROID")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mLocationCallback = object  : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                location = locationResult.lastLocation!!
                if (distance(location.latitude, location.longitude, lastPoint.latitude, lastPoint.longitude) >= 30){
                    mapController.setCenter(GeoPoint(location.latitude, location.longitude))
                    mapController.setZoom(18.0)
                    createPointUpdate(location.latitude, location.longitude, "Tú")
                    lastPoint.latitude = location.latitude
                    lastPoint.longitude = location.longitude
                    drawRoute(lastPoint, friendPosition)
                    updateDB(location)
                }
            }
        }

        pedirPermiso(this, Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita para que la aplicacion funcione", Data.MY_PERMISSION_REQUEST_FINE_LOCATION)

        intent.extras!!.getString("correo")
            ?.let {
                findUserIdByEmail(it) { userId ->
                    if (userId != null) {
                        Log.i("DEBUGMIO", "User ID found: $userId")
                        subscribeToFriendLocationChanges(userId, binding.osmMap)
                    } else {
                        Log.i("DEBUGMIO", "No user found with email: $it")
                    }
                }
            }
    }

    private var roadOverlay: Polyline? = null

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
//        createPointUpdate(finish.latitude, finish.longitude, "")
        val road = roadManager.getRoad(routePoints)
        Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
//        Toast.makeText(this, "Distancia de la ruta: ${"%.2f".format(road.mLength)} km", Toast.LENGTH_SHORT).show()
        Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
        if (binding.osmMap != null) {
            roadOverlay?.let { binding.osmMap.overlays.remove(it) }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay?.outlinePaint?.color = Color.RED
            roadOverlay?.outlinePaint?.strokeWidth = 10f
            binding.osmMap.overlays.add(roadOverlay)
            // Calculate midpoint
            val midLat = (start.latitude + finish.latitude) / 2
            val midLon = (start.longitude + finish.longitude) / 2
            val midPoint = GeoPoint(midLat, midLon)

            // Center the map on the midpoint
            binding.osmMap.controller.setCenter(midPoint)

            // Adjust the zoom level based on the distance
            val distance = road.mLength
            val zoomLevel = calculateZoomLevel(distance)
            binding.osmMap.controller.setZoom(zoomLevel)
        }
    }

    private fun calculateZoomLevel(distance: Double): Double {
        return when {
            distance < 0.1 -> 20.0  // Very close, street-level view
            distance < 0.5 -> 19.0  // Short distance, close-up view
            distance < 1 -> 18.0    // Less than 1 km, still close
            distance < 2 -> 17.0    // Around 1-2 km, neighborhood level
            distance < 5 -> 16.0    // 2-5 km, city-level view
            distance < 10 -> 15.0   // 5-10 km, see a larger area of the city
            distance < 20 -> 14.0   // 10-20 km, overview of a city area
            distance < 30 -> 13.0   // 20-30 km, multiple city neighborhoods
            distance < 50 -> 12.0   // 30-50 km, city and outskirts
            distance < 75 -> 11.0   // 50-75 km, zoomed out city region
            distance < 100 -> 10.0  // 75-100 km, see larger regions
            distance < 150 -> 9.0   // 100-150 km, inter-city connections
            distance < 250 -> 8.0   // 150-250 km, larger city-to-city view
            distance < 500 -> 7.0   // 250-500 km, covering multiple cities
            distance < 1000 -> 6.0  // 500-1000 km, a view covering part of a country
            distance < 2000 -> 5.0  // 1000-2000 km, country-level
            distance < 4000 -> 4.0  // 2000-4000 km, regional level
            distance < 8000 -> 3.0  // 4000-8000 km, continent-level
            else -> 2.0             // Very far distance, global view
        }
    }

    fun createPointUpdate(lat: Double, lon: Double, title: String){
        for (overlay in binding.osmMap.overlays){
            if (overlay is Marker){
                if (overlay.title == "Tú"){
                    binding.osmMap.overlays.remove(overlay)
                }
            }

        }

        val markerPoint = GeoPoint(lat, lon)
        val marker = Marker(binding.osmMap)
        marker.title = title
        val myIcon = resources.getDrawable(R.drawable.baseline_adjust_24, theme)
        marker.icon = myIcon
        Log.i("DEBUGMIO", "Title: " + marker.title.toString())
        marker.position = markerPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.osmMap.overlays.add(marker)
        mapController.setCenter(markerPoint)
        mapController.setZoom(18.0)
    }

    private fun pedirPermiso(context: Activity, permiso: String, justificacion: String, idCode: Int) {

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)

        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            Data.MY_PERMISSION_REQUEST_FINE_LOCATION -> {
                //If request is cancelled, the result arrrays are empty
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    startLocationUpdates()
                }
                return
            }
            else -> {
                binding.osmMap.visibility = View.INVISIBLE
                binding.textoError.text = "Acepta los permisos de ubicación para acceder al mapa"
            }
        }
    }

    private val friendMarkers = mutableMapOf<String, Marker>()

    fun updateFriendMarker(mapView: MapView, friendId: String, latitude: Double, longitude: Double) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(friendId).child("nombre")

        // Fetch the user's name from the database
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.getValue(String::class.java) ?: friendId // Use friendId if name is not available
                val marker = friendMarkers[friendId]

                if (marker == null) {
                    // Create a new marker if it doesn't exist
                    val newMarker = Marker(mapView)
                    friendPosition = GeoPoint(latitude, longitude)
                    newMarker.position = GeoPoint(latitude, longitude)
                    newMarker.title = name // Use the fetched name as the title
                    val myIcon = resources.getDrawable(R.drawable.baseline_boy_24, theme)
                    newMarker.icon = myIcon
                    mapView.overlays.add(newMarker)
                    friendMarkers[friendId] = newMarker
                    Log.i("DEBUGMIO", "FriendID: $friendId with Name: $name")
                } else {
                    removeFriendMarker(mapView, friendId)
                    val newMarker = Marker(mapView)
                    // Update existing marker's position
                    friendPosition = GeoPoint(latitude, longitude)
                    newMarker.position = GeoPoint(latitude, longitude)
                    newMarker.title = name // Use the fetched name as the title
                    val myIcon = resources.getDrawable(R.drawable.baseline_boy_24, theme)
                    newMarker.icon = myIcon
                    mapView.overlays.add(newMarker)
                    friendMarkers[friendId] = newMarker
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching user name for $friendId: ${error.message}")
            }
        })
    }


    fun removeFriendMarker(mapView: MapView, friendId: String) {
        val marker = friendMarkers.remove(friendId)
        Log.i("DEBUGMIO", "Ya no está friendMarkers[friendId]: " + friendMarkers[friendId])
        if (marker != null) {
            Log.i("DEBUGMIO", "Se va a borrar marker: " + marker.toString() + " con titulo " + marker.title)
            mapView.overlays.remove(marker)
//            mapView.invalidate() // Refresh the map
        }
    }

    private fun findUserIdByEmail(email: String, onResult: (String?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    // Extract user data
                    val userEmail = userSnapshot.child("correo").getValue(String::class.java)
                    Log.i("DEBUGMIO", "userEmail: " + userEmail + " vs. email: " + email)
                    if (userEmail == email) {
                        // Return the user ID (key of this snapshot)
                        onResult(userSnapshot.key)
                        return
                    }
                }
                // No matching user found
                onResult(null)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching users: ${error.message}")
                onResult(null)
            }
        })
    }


    private fun subscribeToFriendLocationChanges(friendId: String, mapView: MapView) {
        val database = FirebaseDatabase.getInstance()

            val friendLocationRef = database.getReference("users").child(friendId)
            Log.i("DEBUGMIO", "Reference path: " + friendLocationRef.toString())
            // Listen for latitude changes
            friendLocationRef.child("posicion").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("DEBUGMIO", snapshot.key.toString() + ": " + snapshot.getValue().toString())
                    val posicion = snapshot.getValue(Position::class.java)
                    if (posicion != null) {
//                        friendMarkers[friendId]?.position?.latitude = latitude
                        val lat = posicion.latitud
                        val lon = posicion.longitud
                        Log.i("DEBUGMIO", "Se envían las siguientes latitudes y longitudes para actualizar el amigo: Latitud - " + lat.toString() + " Longitud - " + lon.toString())
                        updateFriendMarker(mapView, friendId, lat!!, lon!!)
                    }
//
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching latitude for $friendId: ${error.message}")
                }
            })

            friendLocationRef.child("friends").addChildEventListener(object : ChildEventListener {
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val friendId = snapshot.getValue(String::class.java)
                    if (friendId != null) {
                        removeFriendMarker(mapView, friendId)
                    }
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error monitoring friends list: ${error.message}")
                }
            })


    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = com.google.android.gms.location.LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(5000)
            .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)

        return locationRequest
    }

    private fun updateDB(location: Location) {
        myRef = database.getReference(Data.PATH_USERS + auth.currentUser!!.uid + "/posicion/latitud")
        myRef.setValue(location.latitude)
        myRef = database.getReference(Data.PATH_USERS + auth.currentUser!!.uid + "/posicion/longitud")
        myRef.setValue(location.longitude)
    }

    private fun startLocationUpdates() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            &&
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val RADIUS_OF_EARTH_KM = 6371.0
        val resultInKilometers = RADIUS_OF_EARTH_KM * c

        // Convert kilometers to meters
        return (resultInKilometers * 1000 * 100.0).roundToInt() / 100.0
    }
}