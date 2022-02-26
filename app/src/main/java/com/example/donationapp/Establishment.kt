package com.example.donationapp

class Establishment(
    val id : String?,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val address: String? = null,
    val phone: String?,
    val description: String? = null
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )

}