package com.example.donationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.donationapp.databinding.ActivityProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var toIdEstablishment : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toIdEstablishment = intent.getStringExtra("toId")!!

        supportActionBar?.title = "Perfil"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchProfile()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchProfile() {

        FirebaseFirestore.getInstance().collection("establishment")
            .document(toIdEstablishment)
            .get()
            .addOnSuccessListener {

                Picasso.get()
                    .load(it.get("photoUrl").toString())
                    .into(binding.imageViewPhoto)

                binding.userName.text = it.get("name").toString()
                binding.textViewPhone.text = it.get("phone").toString()

                if(it.get("description").toString() == "null"){

                    binding.textViewDescription.text = ""
                    binding.textViewDescription.hint = "Este campo não foi preenchido."

                }else{

                    binding.textViewDescription.text = it.get("description").toString()

                }

                if(it.get("address").toString() == "null"){

                    binding.textViewAddress.text = ""
                    binding.textViewAddress.hint = "Este campo não foi preenchido."

                }else{

                    binding.textViewAddress.text = it.get("description").toString()

                }

            }

    }
}