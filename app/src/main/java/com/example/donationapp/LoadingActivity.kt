package com.example.donationapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUserType()
        Log.i("Test", "Chegou na Loading Activity !!!")
    }

    private fun setUserType() {

        val userId = FirebaseAuth.getInstance().uid

        Log.i("TestInit", "userId: $userId e firstTime: ${UniversalCommunication.firstTime}")

        if (userId != null) {
            FirebaseFirestore.getInstance().collection("establishment")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.toObject(Establishment::class.java).id == userId) {
                            UniversalCommunication.userType = "establishment"
                            updateToken("establishment",userId)
                            startTopActivity()
                        }
                    }
                }

            FirebaseFirestore.getInstance().collection("institution")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.toObject(Institution::class.java).id == userId) {
                            UniversalCommunication.userType = "institution"
                            updateToken("institution",userId)
                            startTopActivity()
                        }
                    }
                }

            FirebaseFirestore.getInstance().collection("person")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.toObject(Person::class.java).id == userId) {
                            UniversalCommunication.userType = "person"
                            updateToken("person",userId)
                            startTopActivity()
                        }
                    }
                }
        }
        else{
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {

        // Indo para topActivity
        val intent = Intent(this, MainActivity::class.java)

        //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        Log.i("Test", "Passou na Loading Activity !!!")
        startActivity(intent)

    }

    private fun startTopActivity(){

        // Indo para topActivity
        val intent = Intent(this, TopActivity::class.java)

        //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        Log.i("Test", "Passou na Loading Activity !!!")
        startActivity(intent)

    }

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
}