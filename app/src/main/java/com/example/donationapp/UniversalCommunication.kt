package com.example.donationapp

import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

object UniversalCommunication {

    lateinit var toId : String
    lateinit var userType : String
    const val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"

    fun loadPhoto(url : String, imageView: ImageView){

        Picasso.get()
            .load(url)
            .into(imageView)

    }

    fun createSolicitation(title : String?, description : String?, date : String, fromId : String, toId : String){

        val solicitation = Solicitation()
        solicitation.title = title
        solicitation.description = description
        solicitation.date = date
        solicitation.fromId = fromId
        solicitation.toId = toId

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(fromId)
            .collection(toId)
            .add(solicitation)
            .addOnSuccessListener {

                Log.i("Test","Solicitation added !")

            }.addOnFailureListener {

                Log.e("Test","Error with the solicitation", it)

            }

        FirebaseFirestore.getInstance().collection(this.userType)
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {

                val solicitation2 = Solicitation()
                solicitation2.title = it.get("name").toString()
                solicitation2.description = description
                solicitation2.date = date
                solicitation2.fromId = fromId
                solicitation2.toId = toId

                FirebaseFirestore.getInstance().collection("solicitation")
                    .document(toId)
                    .collection(fromId)
                    .add(solicitation2)
                    .addOnSuccessListener {

                        Log.i("Test", "Solicitation added ! establishment")

                    }.addOnFailureListener {

                        Log.e("Test", "Error with the solicitation", it)

                    }
            }
    }
}