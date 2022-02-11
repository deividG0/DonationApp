package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.flow.merge

class SelectionActivity : AppCompatActivity() {

    private lateinit var autoCompleteText : TextInputLayout
    private lateinit var associationsList : MutableList<String>
    private lateinit var institutionList : MutableList<QueryDocumentSnapshot>
    private lateinit var establishmentList : MutableList<QueryDocumentSnapshot>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        val res = resources

        val buttonCreateInstitution = findViewById<Button>(R.id.buttonCreateInstitution)
        val buttonCreateEstablishment = findViewById<Button>(R.id.buttonCreateEstablishment)
        val buttonForward = findViewById<Button>(R.id.buttonForward)

        buttonForward.setOnClickListener {

            val associationName : String = autoCompleteText.editText?.text.toString()

            if (associationName.isEmpty()) {
                autoCompleteText.error = "Este campo está em branco."
                return@setOnClickListener
            }else{
                autoCompleteText.error = null
            }

            val stringChange : String = String.format(res.getString(R.string.confirmationAlertSelection), associationName)

            MaterialAlertDialogBuilder(this)
                .setMessage(stringChange)
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                }
                .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->

                    setUserAssociation()

                    val intent = Intent(this, TopActivity::class.java)
                    startActivity(intent)

                }
                .show()
        }

        autoCompleteText = findViewById(R.id.autoCompleteText)

        fetchAssociations()

        buttonCreateInstitution.setOnClickListener {

            val intent = Intent(this,CreateInstitutionActivity::class.java)
            startActivity(intent)

        }

        buttonCreateEstablishment.setOnClickListener {

            val intent = Intent(this,CreateEstablishmentActivity::class.java)
            startActivity(intent)

        }

    }

    private fun setUserAssociation() {

        for(inst in institutionList){
            if(inst.toObject(Institution::class.java).name==autoCompleteText.editText?.text.toString()){

                val userId = FirebaseAuth.getInstance().uid

                val associationRef = FirebaseFirestore.getInstance().collection("institution")
                    .document(inst.id)

                FirebaseFirestore.getInstance().collection("users")
                    .document(userId!!)
                    .update("association",associationRef)

            }
        }
        for(est in establishmentList){
            if(est.toObject(Institution::class.java).name==autoCompleteText.editText?.text.toString()){

                val userId = FirebaseAuth.getInstance().uid

                val associationRef = FirebaseFirestore.getInstance().collection("establishment")
                    .document(est.id)

                FirebaseFirestore.getInstance().collection("users")
                    .document(userId!!)
                    .update("association",associationRef)

            }
        }
    }

    private fun fetchAssociations() {

        associationsList = mutableListOf()

        fetchInstitutions()
        fetchEstablishment()

        val adapter = ArrayAdapter(this, R.layout.list_item_selection, associationsList)
        (autoCompleteText.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun fetchInstitutions(){

        institutionList = mutableListOf()

        FirebaseFirestore.getInstance().collection("/institution")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Test", "${document.id} => ${document.data}")

                    institutionList.add(document)

                    val institutionName = document.toObject(Institution::class.java).name.toString()
                    associationsList.add(institutionName)

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Test", "Error getting documents: ", exception)
            }
    }

    private fun fetchEstablishment(){

        establishmentList = mutableListOf()

        FirebaseFirestore.getInstance().collection("/establishment")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Test", "${document.id} => ${document.data}")

                    establishmentList.add(document)

                    val establishmentName = document.toObject(Establishment::class.java).name.toString()
                    associationsList.add(establishmentName)

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Test", "Error getting documents: ", exception)
            }
    }
}