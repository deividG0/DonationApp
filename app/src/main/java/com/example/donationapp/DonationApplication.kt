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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DonationApplication : Application(), Application.ActivityLifecycleCallbacks {

    private fun setOnline(enabled: Boolean) {

        val userId = FirebaseAuth.getInstance().uid

        if (userId != null) {

            FirebaseFirestore.getInstance()
                .collection(UniversalCommunication.userType)
                .document(userId)
                .update("online", enabled)

        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onCreate() {

        super.onCreate()
        Log.i("TestInit", "Entrei no onCreate da Application")

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        setOnline(true)
    }

    override fun onActivityPaused(activity: Activity) {
        setOnline(false)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}