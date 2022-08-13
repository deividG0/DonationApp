package com.example.donationapp

import com.google.android.gms.tasks.Task

class Person(
    val id : String?,
    val name: String?,
    val email : String?,
    val photoUrl: String?,
    val phone: String?,
    val token: String?,
    val online: Boolean
){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        false
    )
}