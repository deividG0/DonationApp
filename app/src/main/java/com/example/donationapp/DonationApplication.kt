package com.example.donationapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class DonationApplication : Application(), Application.ActivityLifecycleCallbacks {

    private fun updateToken(userType: String, currentUserId: String) {

        lateinit var token : String

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

            // Get new FCM registration token
            token = task.result

            UniversalCommunication.userToken = token

            FirebaseFirestore.getInstance().collection(userType)
                .document(currentUserId)
                .update("token", token)

        })

    }

    private fun setOnline(enabled: Boolean) {

        val userId = FirebaseAuth.getInstance().uid

        if (userId != null) {

            FirebaseFirestore.getInstance()
                .collection(UniversalCommunication.userType)
                .document(userId)
                .update("online", enabled)

        }
    }

    private fun setUserType(enabled: Boolean) {

        val userId = FirebaseAuth.getInstance().uid

        if (userId != null) {

            FirebaseFirestore.getInstance().collection("establishment")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.toObject(Establishment::class.java).id == userId) {

                            UniversalCommunication.userType = "establishment"
                            updateToken("establishment", userId)
                            setOnline(enabled)
                            UniversalCommunication.firstTime = false
                            Log.i("Test", "é do tipo estabelecimento")
                            //Toast.makeText(this,"userType: establishment",Toast.LENGTH_SHORT).show()

                        }
                    }
                }

            FirebaseFirestore.getInstance().collection("institution")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.toObject(Institution::class.java).id == userId) {

                            UniversalCommunication.userType = "institution"
                            updateToken("institution", userId)
                            setOnline(enabled)
                            UniversalCommunication.firstTime = false
                            //Toast.makeText(this,"userType: institution",Toast.LENGTH_SHORT).show()

                        }
                    }
                }

            FirebaseFirestore.getInstance().collection("person")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.toObject(Person::class.java).id == userId) {

                            UniversalCommunication.userType = "person"
                            updateToken("person", userId)
                            setOnline(enabled)
                            UniversalCommunication.firstTime = false
                            //Toast.makeText(this,"userType: person",Toast.LENGTH_SHORT).show()

                        }
                    }
                }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {

        if(UniversalCommunication.firstTime){

            this.setUserType(true)

        }else{

            this.setOnline(true)

        }
    }

    override fun onActivityPaused(activity: Activity) {
        this.setOnline(false)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}