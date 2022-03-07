package com.example.donationapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
//import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private val defaultLocation = LatLng(-12.5657828,-43.0288175)
    private var mMap: GoogleMap? = null
    internal lateinit var mLastLocation: Location
    internal var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest
    private lateinit var searchLocationButton: Button
    private lateinit var confirmSearchButton: Button
    private lateinit var cancelSearchButton: Button
    private lateinit var editTextSearchLocation: TextInputLayout
    private var markerSelected: Boolean = false
    private lateinit var location: String
    private lateinit var selectedLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        supportActionBar?.hide()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.location_selection_map) as SupportMapFragment
        mapFragment.getMapAsync (this)

        confirmSearchButton = findViewById(R.id.confirmSearchButton)
        cancelSearchButton = findViewById(R.id.cancelSearchButton)
        editTextSearchLocation = findViewById(R.id.editTextSearchLocation)

        searchLocationButton = findViewById(R.id.searchLocationButton)
        searchLocationButton.setOnClickListener {

            searchLocation(it)

        }

        confirmSearchButton.setOnClickListener {
            //onBackPressed()
            if (!markerSelected){

                Toast.makeText(this,"Procure um endereço",Toast.LENGTH_LONG).show()

            }else {
                val stringChange: String =
                    String.format(getString(R.string.confirmationLocationSelection), location)

                MaterialAlertDialogBuilder(this)
                    .setMessage(stringChange)
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    }
                    .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->

                        // METODO
                        setUserLatLng()
                        Toast.makeText(this, "Endereço confirmado !", Toast.LENGTH_SHORT).show()
                        onBackPressed()

                    }
                    .show()
            }
        }
        cancelSearchButton.setOnClickListener {

            onBackPressed()

        }

    }

    private fun setUserLatLng() {

        val currentUserId = FirebaseAuth.getInstance().uid

        val latLng = Latlng()
        latLng.latitude = selectedLatLng.latitude
        latLng.longitude = selectedLatLng.longitude

        FirebaseFirestore.getInstance().collection(UniversalCommunication.userType)
            .document(currentUserId!!)
            .update(mapOf(
                "latLng" to latLng,
                "address" to location
            ))
            .addOnSuccessListener {

                Log.i("Test", "update in latLng")

            }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true

            }
        }else{

            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true

        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5.5f))
    }

    protected fun buildGoogleApiClient(){

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()

    }

    override fun onLocationChanged(location: Location) {

        mLastLocation = location
        if(mCurrLocationMarker != null){
            mCurrLocationMarker!!.remove()
        }

        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Posição atual")
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(11f))

        if(mGoogleApiClient != null){
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
        {

            LocationServices.getFusedLocationProviderClient(this)

        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    private fun searchLocation(view: View){

        location = editTextSearchLocation.editText?.text.toString()
        var addressList: List<Address>? = null
        if (location == null || location == ""){

            Toast.makeText(this,"Insira uma localização",Toast.LENGTH_SHORT).show()
            editTextSearchLocation.error = "Este campo está em branco."

        }else{

            editTextSearchLocation.error = null

            val geoCoder = Geocoder(this)
            try{
                addressList = geoCoder.getFromLocationName(location,1)
            }catch (e: IOException){
                e.printStackTrace()
            }

            markerSelected = true
            val address = addressList!![0]
            selectedLatLng = LatLng(address.latitude, address.longitude)
            Log.i("Test", "LatLng: $selectedLatLng")
            mMap!!.clear()
            mMap!!.addMarker(MarkerOptions().position(selectedLatLng).title(location))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15.0f));
            //mMap!!.animateCamera(CameraUpdateFactory.newLatLng(selectedLatLng))

        }

    }
}