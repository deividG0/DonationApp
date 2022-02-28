package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateHomeCardActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_home_card)

        val buttonCreateHomeCard = findViewById<Button>(R.id.buttonCreateHomeCard)

        supportActionBar?.title = "Publicação de oferta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progressBarCardCreate)
        progressBar.visibility = View.INVISIBLE

        buttonCreateHomeCard.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            getCurrentUserData()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getCurrentUserData(){

        val currentUserId = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection("establishment")
            .document(currentUserId)
            .get()
            .addOnSuccessListener {

                var establishmentPhotoUrl = it.toObject(Establishment::class.java)?.photoUrl
                var establishmentName = it.toObject(Establishment::class.java)?.name
                setCard(establishmentPhotoUrl,establishmentName,currentUserId)

            }
    }

    private fun setCard(establishmentPhotoUrl: String?, establishmentName: String?, establishmentId: String?) {

        val id: String = UUID.randomUUID().toString()

        val editTextCardDescription = findViewById<TextInputLayout>(R.id.editTextCardDescription)
        val cardDescription : String = editTextCardDescription.editText?.text.toString()
        val timestamp : Long = System.currentTimeMillis()
        val card = HomeCardView(id,establishmentId,establishmentPhotoUrl,establishmentName,cardDescription,timestamp)

        FirebaseFirestore.getInstance().collection("/offer")
            .document(id)
            .set(card)
            .addOnSuccessListener {

                val intent = Intent(this,TopActivity::class.java)
                progressBar.visibility = View.INVISIBLE
                startActivity(intent)
                Log.i("Test","Card created!")

            }.addOnFailureListener {

                Log.i("Test","Card creation failed")

            }
    }
}