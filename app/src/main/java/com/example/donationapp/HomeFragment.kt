package com.example.donationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donationapp.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.awaitAll
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listCards: MutableList<HomeCardView>
    private lateinit var tempListCards: MutableList<HomeCardView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        listCards = mutableListOf()
        tempListCards = mutableListOf()

        binding.progressBarHomeFragment.visibility = View.VISIBLE

        binding.rvHome.setHasFixedSize(true)
        binding.rvHome.layoutManager = LinearLayoutManager(context)

        verifyAssociationType()

        binding.floatingActionButton.hide()

        binding.floatingActionButton.setOnClickListener {

            val intent = Intent(context, CreateHomeCardActivity::class.java)
            startActivity(intent)

        }

        binding.searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBox.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempListCards.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())

                if (searchText.isNotEmpty()) {
                    listCards.forEach {

                        if (it.title!!.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempListCards.add(it)
                        }
                    }
                    binding.rvHome.adapter?.notifyDataSetChanged()
                } else {
                    tempListCards.clear()
                    tempListCards.addAll(listCards)
                    binding.rvHome.adapter?.notifyDataSetChanged()
                }

                return false
            }
        })
        //something wrong here
        binding.offerFilter.recentOfferFilter.setOnClickListener {
            tempListCards.clear()
            tempListCards.addAll((listCards.sortedWith(compareBy { it.timestamp })).asReversed())
            binding.rvHome.adapter?.notifyDataSetChanged()
        }

        binding.offerFilter.oldOfferFilter.setOnClickListener {
            tempListCards.clear()
            tempListCards.addAll(listCards.sortedWith(compareBy { it.timestamp }))

            binding.rvHome.adapter?.notifyDataSetChanged()
        }

        return binding.root
    }

    private fun verifyAssociationType() {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("institution")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    Log.i("Test", "collection institution not existed")
                    searchAmongPerson()

                } else {

                    FirebaseFirestore.getInstance().collection("institution")
                        .get()
                        .addOnSuccessListener {
                            for (doc in it) {
                                if (doc.toObject(Institution::class.java).id == currentUserId) {

                                    Log.i("Test", "verificação is institution")
                                    setupToPersonAndInstitution()
                                    return@addOnSuccessListener

                                }
                            }
                            searchAmongPerson()

                        }.addOnFailureListener {

                            Log.i("Test", "Erro em verifyAssociationType")

                        }
                }
            }
    }

    private fun searchAmongPerson() {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("person")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    Log.i("Test", "collection person not existed")
                    setupToEstablishment()

                } else {

                    FirebaseFirestore.getInstance().collection("person")
                        .get()
                        .addOnSuccessListener {
                            for (doc in it) {
                                if (doc.toObject(Person::class.java).id == currentUserId) {

                                    Log.i("Test", "verificação 2")
                                    setupToPersonAndInstitution()
                                    return@addOnSuccessListener

                                }
                                setupToEstablishment()

                            }
                        }.addOnFailureListener {

                            Log.i("Test", "Erro em searchAmongPerson")

                        }
                }
            }
    }

    private fun setupToPersonAndInstitution() {

        fetchCards()
        binding.floatingActionButton.hide()

    }

    private fun setupToEstablishment() {

        fetchCardsToEstablishment()
        binding.floatingActionButton.show()

    }

    private fun fetchCards() {

        val homeAdapter = HomeAdapter(tempListCards)
        binding.rvHome.adapter = homeAdapter

        FirebaseFirestore.getInstance().collection("offer")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    Log.i("Test", "collection offer not existed")
                    binding.textViewNotification.text = "Nenhuma oferta foi publicada."
                    binding.progressBarHomeFragment.visibility = View.INVISIBLE

                } else {

                    FirebaseFirestore.getInstance().collection("offer")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener {
                            for (doc in it) {

                                listCards.add(doc.toObject(HomeCardView::class.java))

                            }
                            Log.i("Test", "Size list cards ${listCards.size}")
                            tempListCards.addAll(listCards)
                            binding.progressBarHomeFragment.visibility = View.INVISIBLE
                            binding.rvHome.adapter!!.notifyDataSetChanged()
                        }
                }
            }
    }

    private fun fetchCardsToEstablishment() {

        val homeAdapter = HomeAdapterEstablishment(tempListCards)
        binding.rvHome.adapter = homeAdapter

        FirebaseFirestore.getInstance().collection("offer")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    Log.i("Test", "collection offer not existed")
                    binding.textViewNotification.text = "Nenhuma oferta foi publicada."
                    binding.progressBarHomeFragment.visibility = View.INVISIBLE

                } else {
                    FirebaseFirestore.getInstance().collection("offer")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener {
                            for (doc in it) {

                                listCards.add(doc.toObject(HomeCardView::class.java))

                            }
                            Log.i("Test", "Size list cards ${listCards.size}")
                            tempListCards.addAll(listCards)
                            binding.progressBarHomeFragment.visibility = View.INVISIBLE
                            binding.rvHome.adapter!!.notifyDataSetChanged()
                        }
                }
            }
    }
}