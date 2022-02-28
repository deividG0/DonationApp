package com.example.donationapp

import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

object UniversalCommunication {

    lateinit var toId: String
    lateinit var userType: String
    const val defaultProfileImageUrl =
        "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"

    fun loadPhoto(url: String, imageView: ImageView) {

        Picasso.get()
            .load(url)
            .into(imageView)

    }

    fun createSolicitation(
        relatedCardId: String?,
        title: String?,
        photoUrl: String?,
        description: String?,
        date: String,
        fromId: String,
        toId: String
    ) {

        val timestamp: Long = System.currentTimeMillis()

        val id: String = UUID.randomUUID().toString()

        val solicitation = Solicitation()
        solicitation.relatedCardId = relatedCardId
        solicitation.id = id
        solicitation.title = title
        solicitation.photoUrl = photoUrl
        solicitation.description = description
        solicitation.date = date
        solicitation.fromId = fromId
        solicitation.toId = toId
        solicitation.timestamp = timestamp

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(fromId)
            .collection("solicitations")
            .document(id)
            .set(solicitation)
            .addOnSuccessListener {

                Log.i("Test", "Solicitation added !")

            }.addOnFailureListener {

                Log.e("Test", "Error with the solicitation", it)

            }

        FirebaseFirestore.getInstance().collection(this.userType)
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {

                val solicitation2 = Solicitation()
                solicitation2.relatedCardId = relatedCardId
                solicitation2.id = id
                solicitation2.title = it.get("name").toString()
                solicitation2.photoUrl = it.get("photoUrl").toString()
                solicitation2.description = description
                solicitation2.date = date
                solicitation2.fromId = fromId
                solicitation2.toId = toId
                solicitation2.timestamp = timestamp

                FirebaseFirestore.getInstance().collection("solicitation")
                    .document(toId)
                    .collection("solicitations")
                    .document(id)
                    .set(solicitation2)
                    .addOnSuccessListener {

                        Log.i("Test", "Solicitation added ! establishment")

                    }.addOnFailureListener { e ->

                        Log.e("Test", "Error with the solicitation", e)

                    }
            }
    }

}