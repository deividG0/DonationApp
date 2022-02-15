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

        val currentUserId = FirebaseAuth.getInstance().uid
        var institutionId : String?

        FirebaseFirestore.getInstance().collection("/users")
            .document(currentUserId!!)
            .get()
            .addOnSuccessListener {

                institutionId = it.toObject(User::class.java)?.associationId
                Log.i("Test","criação de card inst id: $institutionId")
                Log.i("Test","criação de card inst id 2: ${it.toObject(User::class.java)?.associationId}")
                getAssociationData(institutionId)

            }
            .addOnFailureListener {

                Log.e("Test",it.message, it)

            }
    }

    private fun getAssociationData(institutionId : String?){

        var institutionPhotoUrl : String?
        var institutionName : String?

        FirebaseFirestore.getInstance().collection("/institution")
            .document(institutionId!!)
            .get()
            .addOnSuccessListener {

                institutionPhotoUrl = it.toObject(Institution::class.java)?.photoUrl
                institutionName = it.toObject(Institution::class.java)?.name
                setCard(institutionPhotoUrl,institutionName,institutionId)

            }
    }

    private fun setCard(institutionPhotoUrl: String?, institutionName: String?, institutionId: String?) {

        val editTextCardDescription = findViewById<TextInputLayout>(R.id.editTextCardDescription)
        val cardDescription : String = editTextCardDescription.editText?.text.toString()
        val timestamp : Long = System.currentTimeMillis()
        val card = HomeCardView(institutionId,institutionPhotoUrl,institutionName,cardDescription,timestamp)

        FirebaseFirestore.getInstance().collection("/requirements")
            .add(card)
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