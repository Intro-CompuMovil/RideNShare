package com.example.ridenshare.Logica

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridenshare.Data.DataTest
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityRutaDesdeMapaBinding
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
import org.osmdroid.views.overlay.TilesOverlay

class RutaDesdeMapaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRutaDesdeMapaBinding
    lateinit var roadManager: RoadManager
    val route = DataTest.Route("", mutableListOf())
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightSensorListener: SensorEventListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRutaDesdeMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().setUserAgentValue(applicationContext.packageName)

        binding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.osmMap.setMultiTouchControls(true)

        roadManager = OSRMRoadManager(this, "ANDROID")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding.osmMap.overlays.add(createOverlayEvents())

        val mapController: IMapController = binding.osmMap.controller
        val bogotaCenter = GeoPoint(4.7110, -74.0721)
        mapController.setCenter(bogotaCenter)
        mapController.setZoom(13.0)

        binding.button.setOnClickListener{
            route.titulo = binding.titleInput.text.toString()
            DataTest.routes.add(route)
            Toast.makeText(this, "Ruta creada con éxito", Toast.LENGTH_SHORT).show()
            startActivity(Intent(baseContext, myRoutes::class.java))
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                Log.i("MAPldksaj", "Valor: ${event.values[0]}")
                if (event.values[0] < 500) {
                    binding.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

                } else {
                    binding.osmMap.overlayManager.tilesOverlay.setColorFilter(null)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
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
        route.points.add(DataTest.Point(p.latitude, p.longitude))
        longPressedMarker = createMarker(p, geoCoderSearchLatLang(p), null, R.drawable.baseline_adb_24)
        longPressedMarker?.let {
            binding.osmMap.overlays.add(it)
            drawRoute()
        }
    }

    private fun geoCoderSearchLatLang(latLng: GeoPoint): String? {
        val mGeocoder = Geocoder(this)
        return mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.get(0)?.getAddressLine(0)
    }

    private fun createMarker(p: GeoPoint, title: String?, desc: String?, iconID: Int): Marker? {
        var marker: Marker? = null
        if (binding.osmMap != null) {
            marker = Marker(binding.osmMap)
            title?.let { marker.title = it }
            desc?.let { marker.subDescription = it }
            if (iconID != 0) {
                val myIcon = resources.getDrawable(iconID, this.theme)
                marker.icon = myIcon
            }
            marker.position = p
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        return marker
    }

    private var roadOverlay: Polyline? = null
    private fun drawRoute() {
        val routePoints = ArrayList<GeoPoint>()
        for (point in route.points) {
            val geoPoint = GeoPoint(point.latitude, point.longitude)
            routePoints.add(geoPoint)
        }
        val road = roadManager.getRoad(routePoints)
        if (binding.osmMap != null) {
            roadOverlay?.let { binding.osmMap.overlays.remove(it) }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay?.outlinePaint?.color = Color.RED
            roadOverlay?.outlinePaint?.strokeWidth = 10f
            binding.osmMap.overlays.add(roadOverlay)
        }
    }
}