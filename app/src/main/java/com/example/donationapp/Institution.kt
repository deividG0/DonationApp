package com.example.donationapp

class Institution(
    val name: String?,
    val photoUrl: String?,
    val type: String?,
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
        "",
        mutableListOf()
    )

}