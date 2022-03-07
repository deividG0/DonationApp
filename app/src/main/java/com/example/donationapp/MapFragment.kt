package com.example.donationapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.donationapp.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapFragment : Fragment() {

    private lateinit var googleMapsFragment: SupportMapFragment
    private lateinit var binding: FragmentMapBinding
    private val defaultLocation = LatLng(-12.5657828,-43.0288175)

    private var places = mutableListOf<Place>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.progressBarMapFragment.visibility = View.VISIBLE

        //Impossibilitando toque na tela pelo usuário
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        googleMapsFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        googleMapsFragment.getMapAsync { googleMap ->

            fetchMarkers(googleMap)
            googleMap.setOnMapLoadedCallback {

                if (places.size<=1) {

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5.5f))

                } else {

                    val bounds = LatLngBounds.builder()
                    places.forEach { place ->

                        bounds.include(place.latLng)

                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150))
                    binding.progressBarMapFragment.visibility = View.INVISIBLE

                    //Permitindo toque na tela novamente
                    activity?.window?.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                }
            }
        }

        return binding.root
    }

    private fun fetchMarkers(googleMap: GoogleMap) {

        FirebaseFirestore.getInstance().collection("establishment")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    val establishment = doc.toObject(Establishment::class.java)
                    if (establishment.address != null && establishment.address != "") {

                        places.add(
                            Place(
                                establishment.name,
                                LatLng(establishment.latLng.latitude!!, establishment.latLng.longitude!!),
                                establishment.address
                            )
                        )

                    }
                }
                FirebaseFirestore.getInstance().collection("institution")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (doc in documents) {
                            val institution = doc.toObject(Institution::class.java)
                            if (institution.address != null && institution.address != "") {

                                places.add(
                                    Place(
                                        institution.name,
                                        LatLng(institution.latLng.latitude!!, institution.latLng.longitude!!),
                                        institution.address
                                    )
                                )

                            }
                        }
                        addMarkers(googleMap)
                    }
            }
    }

    private fun addMarkers(googleMap: GoogleMap) {

        places.forEach {
            googleMap.addMarker(
                MarkerOptions().title(it.name)
                    .snippet(it.address)
                    .position(it.latLng)

            )
        }

    }

    data class Place(

        val name: String?,
        val latLng: LatLng,
        val address: String,

        )
}