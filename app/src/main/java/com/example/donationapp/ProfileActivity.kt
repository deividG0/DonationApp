package com.example.donationapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.donationapp.databinding.ActivityProfileBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var toIdEstablishment : String
    private lateinit var title : String
    private lateinit var photoUrl : String
    private lateinit var description : String
    private val currentId = FirebaseAuth.getInstance().uid
    private lateinit var establishmentId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toIdEstablishment = intent.getStringExtra("toId")!!

        binding.progressBarProfileAct.visibility = View.VISIBLE

        //Impossibilitando toque na tela pelo usuário
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        supportActionBar?.title = "Perfil"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchProfile()

        binding.buttonPartnershipRequest.setOnClickListener {

            showDialogInput()

        }

    }

    private fun showDialogInput() {

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.partnership_dialog_input)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendarViewSolicitationPartnership)

        val sdf = SimpleDateFormat("dd/M/yyyy")
        var date : String = sdf.format(calendarView.date)

        calendarView.minDate = System.currentTimeMillis() - 1000

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            Toast.makeText(
                this,
                "Data: $dayOfMonth/${month + 1}/$year",
                Toast.LENGTH_LONG
            ).show()

            date = "$dayOfMonth/${month + 1}/$year"

        }

        val inputDialogText = dialog.findViewById<TextInputLayout>(R.id.inputDialogTextPartnership)

        val cancelButtonDialog = dialog.findViewById<Button>(R.id.cancelButtonDialogPartnership)
        val confirmButtonDialog = dialog.findViewById<Button>(R.id.confirmButtonDialogPartnership)

        inputDialogText.error = null

        confirmButtonDialog.setOnClickListener {

            if (inputDialogText.editText?.text!!.isEmpty()) {

                inputDialogText.error = "Este campo não foi preenchido"
                return@setOnClickListener

            }

            description = inputDialogText.editText?.text!!.toString()

            UniversalCommunication.createSolicitation(
                null,
                title,
                photoUrl,
                description,
                date,
                currentId!!,
                establishmentId
            )

            Toast.makeText(this, "Solicitação enviada !", Toast.LENGTH_LONG)
                .show()

            Log.i("Test", inputDialogText.editText?.text.toString())
            dialog.dismiss()

        }
        cancelButtonDialog.setOnClickListener { dialog.cancel() }

        dialog.show()

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

                establishmentId = it.get("id").toString()
                photoUrl = it.get("photoUrl").toString()
                title = it.get("name").toString()

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

                    binding.textViewAddress.text = it.get("address").toString()

                }

                binding.progressBarProfileAct.visibility = View.INVISIBLE
                //Permitindo toque na tela novamente
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            }
    }
}