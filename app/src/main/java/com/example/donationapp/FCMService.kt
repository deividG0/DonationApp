package com.example.donationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    fun notify(ii: Intent, data: Map<String, String>) {

        val pIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, ii, 0)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val CHANNEL_ID = "channel_id_01"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChanel: NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChanel.description = "Channel description"
            notificationChanel.enableLights(true)
            notificationChanel.lightColor = Color.GREEN
            notificationManager.createNotificationChannel(notificationChanel)
        }
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        builder.setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Solicitação de oferta")
            .setContentText((data.get("title")) + " está solicitando uma de suas ofertas.")
            .setContentIntent(pIntent)

        notificationManager.notify(1, builder.build())

    }

    override fun onMessageReceived(p0: RemoteMessage) {

        Log.i("Test", p0.messageId.toString())

        val data = p0.data

        //Log.i("Test", p0.messageId.toString())

        if (data == null || data.get("sender") == null) return

        val ii = Intent(this, SolicitationActivity::class.java)

        notify(ii, data)

        /*

        FirebaseFirestore.getInstance().collection("institution")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    if (doc.toObject(Institution::class.java).id == data.get("sender")) {

                        notify(ii,data)

                    }
                }
            }

        FirebaseFirestore.getInstance().collection("person")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    if (doc.toObject(Person::class.java).id == data.get("sender")) {

                        notify(ii,data)

                    }
                }
            }

         */

    }
}