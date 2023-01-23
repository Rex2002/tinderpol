package de.dhbw.tinderpol

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.R.drawable.*
import de.dhbw.tinderpol.databinding.ActivityMapsBinding
import android.content.Context
import java.io.IOException
import com.google.gson.*
import de.dhbw.tinderpol.data.room.countryRoadTakeMeHome
import de.dhbw.tinderpol.data.room.place
import de.dhbw.tinderpol.util.OnSwipeTouchListener
import de.dhbw.tinderpol.SwipeActivity





class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /*fun getCountryID(){

    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


    fun getJsonData(){
        val jsonFileString = getJsonDataFromAsset(applicationContext, "countries.json")

        //Länderkürzel holen!!

        val country = "DE"

        var gson = Gson().fromJson(jsonFileString, countryRoadTakeMeHome::class.java)
        val stringAsshole = gson.filter(country)
        var myPlace = Gson().fromJson(stringAsshole, place::class.java)
    }
    */


    //var place = gson.get(country)
    //var place = Gson().fromJson(gson.get(country), countryRoadTakeMeHome::class.java)
    //var gson=Gson()
    //var place = gson.fromJson(jsonFileString, countryRoadTakeMeHome::class.java)

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    /*fun getLat(){
        val natID = getCountryID()
        val lo=0.0
         }


    fun getLong(){
        val natID = getCountryID()
        val lan = 0.0
    }*/
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val lat = 0.0 //getLat()
        val lon = 0.0 // getLong()
        val location = LatLng(lat,lon)
        mMap.addMarker(MarkerOptions().position(location).title("Former Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }}

//need to be able to leave map after

