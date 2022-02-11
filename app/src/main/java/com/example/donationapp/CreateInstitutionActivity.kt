package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateInstitutionActivity : AppCompatActivity() {

    private val defaultProfileImageUrl =
        "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"

    private lateinit var editTextNameInstitution: TextInputLayout
    private lateinit var editTextAddressInstitution: TextInputLayout
    private lateinit var editTextPhoneInstitution: TextInputLayout
    private lateinit var editTextTypeInstitution: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_institution)

        val buttonRegisterInstitution = findViewById<Button>(R.id.buttonRegisterInstitution)

        editTextNameInstitution = findViewById(R.id.editTextNameInstitution)
        editTextAddressInstitution = findViewById(R.id.editTextAddressInstitution)
        editTextPhoneInstitution = findViewById(R.id.editTextPhoneInstitution)
        editTextTypeInstitution = findViewById(R.id.editTextTypeInstitution)

        buttonRegisterInstitution.setOnClickListener {

            createInstitution()

        }
    }

    private fun createInstitution() {

        var institutionId: String

        val name = editTextNameInstitution.editText?.text.toString()
        val address = editTextAddressInstitution.editText?.text.toString()
        val phone = editTextPhoneInstitution.editText?.text.toString()
        val type = editTextTypeInstitution.editText?.text.toString()
        val userAdminId = FirebaseAuth.getInstance().uid

        val institution = Institution(
            name, defaultProfileImageUrl, type, address, phone, userAdminId!!,
            mutableListOf()
        )

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || type.isEmpty()) {

            if (name.isEmpty()) {
                editTextNameInstitution.error = "Este campo está em branco."
            } else {
                editTextNameInstitution.error = null
            }
            if (address.isEmpty()) {
                editTextAddressInstitution.error = "Este campo está em branco."
            } else {
                editTextAddressInstitution.error = null
            }
            if (phone.isEmpty()) {
                editTextPhoneInstitution.error = "Este campo está em branco."
            } else {
                editTextPhoneInstitution.error = null
            }
            if (type.isEmpty()) {
                editTextTypeInstitution.error = "Este campo está em branco."
            } else {
                editTextTypeInstitution.error = null
            }
            return

        }

        FirebaseFirestore.getInstance().collection("institution")
            .add(institution)
            .addOnSuccessListener {

                institutionId = it.id

                setUserAssociation(institutionId)

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

    private fun setUserAssociation(institutionId: String) {

        val userId = FirebaseAuth.getInstance().uid

        val associationRef = FirebaseFirestore.getInstance().collection("institution")
            .document(institutionId)

        FirebaseFirestore.getInstance().collection("users")
            .document(userId!!)
            .update("association", associationRef)

    }
}