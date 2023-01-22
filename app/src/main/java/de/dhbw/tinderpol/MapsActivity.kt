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
import de.dhbw.tinderpol.data.Notice
import de.dhbw.tinderpol.util.OnSwipeTouchListener
import com.google.android.material.R.drawable.*


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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    /*
     * this is probably a lot harder and less efficient than it has to be
     * but i think the API only lets you use your own location, a pre saved one or you add one
     * while youre on the map. Therefore a few countries will be hardcoded
    */

    /*fun getLo(){
        if(AF){lo = 33.927125}
        else if(EG){lo = 26.820190}
        else if(AR){lo = -38.416097}
        else if(AU){lo = -25.274022}
        else if(BE){lo = 50.8504500}
        else if(BG){lo = 42.733883}
        else if(CN){lo = 19.7699300}
        else if(DE){lo = 51.165691}
        else if(EU){lo = 50,02447}
        else if(RU){lo = 51.8590500}
        return lo
         }


    fun getLan(){
        if(AF){lan = 67.721655}
        else if(EG){lan = 30.798712}
        else if(AR){lan = -63.616672}
        else if(AU){lan = 133.775392}
        else if(BE){lan = 4.3487800}
        else if(BG){lan = 25.485830}
        else if(CN){lan = -90.4959500}
        else if(DE){lan = 10.451526}
        else if(EU){lan = 9,9319}
        else if(RU){lan = 58.2213600}
        return lan
    }*/
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val lo = 0.0 //getLo()
        val lan = 0.0 // getLan()
        val location = LatLng(lo,lan)
        mMap.addMarker(MarkerOptions().position(location).title("Former Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
/*
need to be able to leave map after

    binding.root.setOnTouchListener(object : OnSwipeTouchListener(this){
        override fun onSwipeLeft() {
            super.onSwipeLeft()
            val notice = SDO.getNextNotice()
            val nameText = "${notice.firstName} ${notice.lastName} (${notice.sex})"
            binding.textViewFullName.text = nameText
            SwipeActivity.updateShownImg()
        }}*/
}