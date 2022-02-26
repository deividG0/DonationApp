package com.example.donationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.donationapp.databinding.ActivityRequestBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val editTextSelectDate: TextInputLayout = binding.editTextSelectDate
        var date : Long

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()

        editTextSelectDate.setOnClickListener {
            Log.i("Test","Abrir calendar")
        }

        editTextSelectDate.setOnClickListener {

            Log.i("Test","Abrir calendar")

            datePicker.show(supportFragmentManager, "tag")

        }

        datePicker.addOnPositiveButtonClickListener {

            date = datePicker.selection!!
            Toast.makeText(this,"Printando $date", Toast.LENGTH_LONG).show()

        }
        datePicker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
        }

    }
}