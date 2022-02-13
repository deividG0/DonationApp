package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateEstablishmentActivity : AppCompatActivity() {

    private val defaultProfileImageUrl =
        "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"

    private lateinit var editTextNameEstablishment: TextInputLayout
    private lateinit var editTextAddressEstablishment: TextInputLayout
    private lateinit var editTextPhoneEstablishment: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_establishment)

        val buttonRegisterEstablishment = findViewById<Button>(R.id.buttonRegisterEstablishment)

        editTextNameEstablishment = findViewById(R.id.editTextNameEstablishment)
        editTextAddressEstablishment = findViewById(R.id.editTextAddressEstablishment)
        editTextPhoneEstablishment = findViewById(R.id.editTextPhoneEstablishment)

        buttonRegisterEstablishment.setOnClickListener {

            createEstablishment()

        }

    }

    private fun createEstablishment() {

        var establishmentId: String

        val name = editTextNameEstablishment.editText?.text.toString()
        val address = editTextAddressEstablishment.editText?.text.toString()
        val phone = editTextPhoneEstablishment.editText?.text.toString()
        val userAdminId = FirebaseAuth.getInstance().uid

        val establishment = Establishment(
            name, defaultProfileImageUrl, address, phone, userAdminId!!,
            mutableListOf()
        )

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {

            if (name.isEmpty()) {
                editTextNameEstablishment.error = "Este campo está em branco."
            } else {
                editTextNameEstablishment.error = null
            }
            if (address.isEmpty()) {
                editTextAddressEstablishment.error = "Este campo está em branco."
            } else {
                editTextAddressEstablishment.error = null
            }
            if (phone.isEmpty()) {
                editTextPhoneEstablishment.error = "Este campo está em branco."
            } else {
                editTextPhoneEstablishment.error = null
            }
            return

        }

        FirebaseFirestore.getInstance().collection("establishment")
            .add(establishment)
            .addOnSuccessListener {

                establishmentId = it.id

                setUserAssociation(establishmentId)

                val intent = Intent(this, TopActivity::class.java)

                //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                //Iniciando nova atividade
                startActivity(intent)

            }.addOnFailureListener {

                Toast.makeText(
                    this,
                    "Ocorreu um erro durante ao registrar a instituição.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.e("Test", it.message.toString(), it)

            }
    }

    private fun setUserAssociation(establishmentId: String) {

        val userId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("users")
            .document(userId!!)
            .update("associationId", establishmentId)

    }
}