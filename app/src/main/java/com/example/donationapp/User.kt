package com.example.donationapp

import com.google.firebase.firestore.DocumentReference

class User(val id: String?, val name: String?, val email: String?, val associationId: String? = null){

    constructor() : this(
        "",
        "",
        "",
        "")

}