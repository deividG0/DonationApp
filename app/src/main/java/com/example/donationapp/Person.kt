package com.example.donationapp

class Person(
    val id : String?,
    val name: String?,
    val email : String?,
    val photoUrl: String?,
    val phone: String?,
){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
    )
}