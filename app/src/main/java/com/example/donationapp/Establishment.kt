package com.example.donationapp

import com.google.android.gms.maps.model.LatLng

class Establishment(
    val id : String?,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val address: String? = null,
    val latLng: Latlng,
    val phone: String?,
    val description: String? = null
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        Latlng(),
        "",
        ""
    )

}