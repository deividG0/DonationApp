package com.example.donationapp

import com.google.firebase.firestore.FirebaseFirestore

class HomeCardView(
    val id: String? = null,
    val establishmentId: String?,
    val photoUrl: String?,
    val title: String?,
    val description: String?,
    var timestamp: Long? = null
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        ""
    )
}