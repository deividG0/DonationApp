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

class CreateHomeCardActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_home_card)

        val buttonCreateHomeCard = findViewById<Button>(R.id.buttonCreateHomeCard)

        progressBar = findViewById(R.id.progressBarCardCreate)
        progressBar.visibility = View.INVISIBLE

        buttonCreateHomeCard.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            createCard()

        }
    }

    private fun createCard() {

        val editTextCardDescription = findViewById<TextInputLayout>(R.id.editTextCardDescription)
        val currentUserId = FirebaseAuth.getInstance().uid
        var institutionId : String? = null
        var institutionPhotoUrl : String? = null
        var institutionName : String? = null
        val cardDescription : String = editTextCardDescription.editText?.text.toString()

        FirebaseFirestore.getInstance().collection("/users")
            .document(currentUserId!!)
            .get()
            .addOnSuccessListener {

                institutionId = it.toObject(User::class.java)?.associationId
                Log.i("Test","criação de card inst id: $institutionId")
                Log.i("Test","criação de card inst id 2: ${it.toObject(User::class.java)?.associationId}")

            }
            .addOnFailureListener {

                Log.e("Test",it.message, it)

            }

        FirebaseFirestore.getInstance().collection("/institution")
            .document(institutionId!!)
            .get()
            .addOnSuccessListener {

                institutionPhotoUrl = it.toObject(Institution::class.java)?.photoUrl
                institutionName = it.toObject(Institution::class.java)?.name

            }

        val card = HomeCardView(institutionPhotoUrl,institutionName,cardDescription)

        FirebaseFirestore.getInstance().collection("/institution")
            .document(institutionId!!)
            .collection("/requirements")
            .add(card)
            .addOnSuccessListener {

                Log.i("Test","Card created!")

            }.addOnFailureListener {

                Log.i("Test","Card creation failed")

            }

        val intent = Intent(this,TopActivity::class.java)
        progressBar.visibility = View.INVISIBLE
        startActivity(intent)

    }
}