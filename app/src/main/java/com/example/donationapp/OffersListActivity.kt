package com.example.donationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donationapp.databinding.ActivityOffersListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OffersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOffersListBinding
    private lateinit var listCards: MutableList<HomeCardView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOffersListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Minhas ofertas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listCards = mutableListOf()

        binding.progressBarOffersListActivity.visibility = View.VISIBLE

        binding.rvOffersListActivity.setHasFixedSize(true)
        binding.rvOffersListActivity.layoutManager = LinearLayoutManager(this)

        fetchCardsToEstablishment()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchCardsToEstablishment() {

        val offersListActivityAdapter = OffersListActivityAdapter(listCards)
        binding.rvOffersListActivity.adapter = offersListActivityAdapter
        val userId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("offer")
            .whereEqualTo("establishmentId", userId)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    Log.i("Test", "collection offer not existed")
                    binding.textViewNotification.text = "Nenhuma oferta foi publicada."
                    binding.progressBarOffersListActivity.visibility = View.INVISIBLE

                } else {
                    for (doc in it) {
                        val card = doc.toObject(HomeCardView::class.java)
                        listCards.add(card)
                        listCards.sortedWith(compareBy { offer -> offer.timestamp })
                    }
                    binding.progressBarOffersListActivity.visibility = View.INVISIBLE
                    binding.rvOffersListActivity.adapter!!.notifyDataSetChanged()
                }
            }
    }
}