package com.example.donationapp

class Establishment(
    val name: String?,
    val photoUrl: String?,
    val address: String?,
    val phone: String?,
    val userAdminId: String,
    val partnerships: MutableList<String>
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        mutableListOf()
    )

}