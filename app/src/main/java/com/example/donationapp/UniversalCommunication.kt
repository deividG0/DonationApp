package com.example.donationapp

import android.util.Log
import android.widget.ImageView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

object UniversalCommunication {

    lateinit var toId: String
    var firstTime = true
    lateinit var userType: String
    lateinit var userToken: String
    var isUserOnline: Boolean = false
    const val defaultProfileImageUrl =
        "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var badgeSolicitation: BadgeDrawable

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
        toId: String,
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

                this.createBadgeSolicitation()
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

                FirebaseFirestore.getInstance().collection("establishment")
                    .document(toId)
                    .get()
                    .addOnSuccessListener { e ->
                        val establishment = e.toObject(Establishment::class.java)
                        if (establishment?.online == false) {

                            val solicitationNotification = SolicitationNotification()
                            solicitationNotification.fromId = fromId
                            solicitationNotification.toId = toId
                            solicitationNotification.title = it.get("name").toString()
                            solicitationNotification.description = description
                            solicitationNotification.date = date
                            solicitationNotification.timestamp = timestamp

                            FirebaseFirestore.getInstance().collection("solicitationNotifications")
                                .document(establishment.token!!)
                                .set(solicitationNotification)

                        }
                    }
            }
    }

    fun createBadgeSolicitation() {

        var solicitationQuantity: Int = 0
        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("solicitation").get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    badgeSolicitation = bottomNavigation.getOrCreateBadge(R.id.profile)
                    badgeSolicitation.isVisible = true
                    // An icon only badge will be displayed unless a number is set:
                    badgeSolicitation.number = solicitationQuantity
                }
            }

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(currentUserId!!)
            .collection("solicitations")
            .get()
            .addOnSuccessListener {

                for (doc in it) {

                    solicitationQuantity++

                }
                if (solicitationQuantity > 0) {
                    badgeSolicitation = bottomNavigation.getOrCreateBadge(R.id.profile)
                    badgeSolicitation.isVisible = true
                    // An icon only badge will be displayed unless a number is set:
                    badgeSolicitation.number = solicitationQuantity
                }
            }
    }

    fun cleanBadgeSolicitation() {

        badgeSolicitation.isVisible = false
        badgeSolicitation.clearNumber()

    }

    fun createChatNotification(
        toId: String,
        fromId: String,
        nameSender: String,
        timestamp: Long,
        text: String
    ) {

        FirebaseFirestore.getInstance().collection("establishment")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    val e = doc.toObject(Establishment::class.java)
                    if (e.id == toId && !e.online) {

                        val chatNotification = ChatNotification()
                        chatNotification.fromId = fromId
                        chatNotification.toId = e.id
                        chatNotification.nameSender = nameSender
                        chatNotification.text = text
                        chatNotification.timestamp = timestamp.toString()

                        FirebaseFirestore.getInstance().collection("chatNotifications")
                            .document(e.token!!)
                            .set(chatNotification)

                    }
                }
            }

        FirebaseFirestore.getInstance().collection("institution")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    val i = doc.toObject(Institution::class.java)
                    if (i.id == toId && !i.online) {

                        val chatNotification = ChatNotification()
                        chatNotification.fromId = fromId
                        chatNotification.toId = i.id
                        chatNotification.nameSender = nameSender
                        chatNotification.text = text
                        chatNotification.timestamp = timestamp.toString()

                        FirebaseFirestore.getInstance().collection("chatNotifications")
                            .document(i.token!!)
                            .set(chatNotification)

                    }
                }
            }

        FirebaseFirestore.getInstance().collection("person")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    val p = doc.toObject(Person::class.java)
                    if (p.id == toId && !p.online) {

                        val chatNotification = ChatNotification()
                        chatNotification.fromId = fromId
                        chatNotification.toId = p.id
                        chatNotification.nameSender = nameSender
                        chatNotification.text = text
                        chatNotification.timestamp = timestamp.toString()

                        FirebaseFirestore.getInstance().collection("chatNotifications")
                            .document(p.token!!)
                            .set(chatNotification)

                    }
                }
            }
    }
}