package de.dhbw.tinderpol

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.dhbw.tinderpol.databinding.ActivityMapsBinding
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import de.dhbw.tinderpol.data.Country
import de.dhbw.tinderpol.data.MapsData


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val nationalityColor = BitmapDescriptorFactory.HUE_VIOLET
        private const val chargedColor = BitmapDescriptorFactory.HUE_RED
        private const val birthColor = BitmapDescriptorFactory.HUE_CYAN
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("mapsActivity", "creating instance")
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private fun addMarker(country: Country?, color: Float, title: String) {
        if (country == null) return
        val lat = country.lat
        val lon = country.long
        val location = LatLng(lat,lon)
        mMap.addMarker(MarkerOptions()
            .position(location)
            .title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(color)))
        Log.i("mapsActivity", "added marker $title at \n lat: $lat \n long: $lon")
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val data = MapsData.deserialize(intent.getStringExtra("data"))
        Log.i("Countries", data.toString())
        Log.i("Countries", data.chargeCountries.toString())

        addMarker(SDO.getCountry(data.birthCountry), birthColor, "Birth Country")
        data.nationalities.forEach { addMarker(SDO.getCountry(it), nationalityColor, "Nationality") }
        data.chargeCountries.forEach { addMarker(SDO.getCountry(it), chargedColor, "Charged by") }

        val birthCountry = SDO.getCountry(data.birthCountry)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(birthCountry?.lat ?: 0.0, birthCountry?.long ?: 0.0)))
    }}

