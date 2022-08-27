package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoadingActivity : AppCompatActivity() {

    private var userId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = FirebaseAuth.getInstance().uid
        val intent: Intent = if (userId == null){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, TopActivity::class.java)
        }

        //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK


        setUserType(intent)
        Log.i("Test", "Passou pela Loading Activity !!!")
    }

    private fun setUserType(i: Intent) {

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
                            startActivity(i)
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
                            startActivity(i)
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
                            startActivity(i)
                        }
                    }
                }
        }
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