package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, TopActivity::class.java)

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
                            startActivity(i)
                        }
                    }
                }
        }
    }

}