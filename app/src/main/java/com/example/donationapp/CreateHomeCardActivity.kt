package com.example.donationapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu_create_card, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.info -> showInformation(R.string.create_card_info)

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInformation(informationText : Int){

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.information_dialog)

        dialog.findViewById<TextView>(R.id.textViewInfo).text = resources.getString(informationText)

        dialog.setCancelable(true)

        dialog.show()

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
        val card = HomeCardView(id,establishmentId,establishmentPhotoUrl,0,establishmentName,cardDescription,timestamp)

        editTextCardDescription.error = null

        if (cardDescription == null || cardDescription == ""){

            editTextCardDescription.error = "Este campo está em branco."
            progressBar.visibility = View.INVISIBLE
            return

        }

        FirebaseFirestore.getInstance().collection("/offer")
            .document(id)
            .set(card)
            .addOnSuccessListener {

                val intent = Intent(this,TopActivity::class.java)
                progressBar.visibility = View.INVISIBLE
                //finish()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                Log.i("Test","Card created!")

            }.addOnFailureListener {

                Log.i("Test","Card creation failed")

            }
    }
}