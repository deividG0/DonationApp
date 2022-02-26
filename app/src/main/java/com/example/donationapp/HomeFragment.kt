package com.example.donationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donationapp.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.awaitAll

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listCards: MutableList<HomeCardView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        listCards = mutableListOf()

        binding.progressBarHomeFragment.visibility = View.VISIBLE

        binding.rvHome.setHasFixedSize(true)
        binding.rvHome.layoutManager = LinearLayoutManager(context)

        verifyAssociationType()

        binding.floatingActionButton.hide()

        binding.floatingActionButton.setOnClickListener {

            val intent = Intent(context, CreateHomeCardActivity::class.java)
            startActivity(intent)

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

                                    Log.i("Test", "verificação 1")
                                    setupToPersonAndInstitution()

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
        Log.i("Test", "CHEGUEI ATÉ AQUI institution and person")

    }

    private fun setupToEstablishment() {

        fetchCardsToEstablishment()
        binding.floatingActionButton.show()
        Log.i("Test", "CHEGUEI ATÉ AQUI establishment")

    }

    private fun fetchCards() {

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

                                /*Log.i("Test","Chegou em offers pelo menos")

                                var photoUrl: String?
                                var title: String?
                                val establishmentId =
                                    doc.toObject(HomeCardView::class.java).establishmentId
                                val description =
                                    doc.toObject(HomeCardView::class.java).description
                                val timestamp =
                                    doc.toObject(HomeCardView::class.java).timestamp

                                FirebaseFirestore.getInstance().collection("establishment")
                                    .document(establishmentId!!)
                                    .get()
                                    .addOnSuccessListener { establishment ->

                                        Log.i("Test","Atualizou cards")

                                        photoUrl = establishment.toObject(Establishment::class.java)?.photoUrl
                                        title = establishment.toObject(Establishment::class.java)?.name

                                        val homeCardView = HomeCardView(
                                            establishmentId,
                                            photoUrl,
                                            title,
                                            description,
                                            timestamp
                                        )

                                        listCards.add(homeCardView)

                                    }*/

                                listCards.add(doc.toObject(HomeCardView::class.java))

                            }
                            //Log.i("Test","list card rv: ${listCards.toString()}")
                            binding.progressBarHomeFragment.visibility = View.INVISIBLE
                            val homeAdapter = HomeAdapter(listCards)
                            binding.rvHome.adapter = homeAdapter
                        }
                }
            }
    }

    private fun fetchCardsToEstablishment() {

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

                                /*Log.i("Test","Chegou em offers pelo menos")

                                var photoUrl: String?
                                var title: String?
                                val establishmentId =
                                    doc.toObject(HomeCardView::class.java).establishmentId
                                val description =
                                    doc.toObject(HomeCardView::class.java).description
                                val timestamp =
                                    doc.toObject(HomeCardView::class.java).timestamp

                                FirebaseFirestore.getInstance().collection("establishment")
                                    .document(establishmentId!!)
                                    .get()
                                    .addOnSuccessListener { establishment ->

                                        Log.i("Test","Atualizou cards")

                                        photoUrl = establishment.toObject(Establishment::class.java)?.photoUrl
                                        title = establishment.toObject(Establishment::class.java)?.name

                                        val homeCardView = HomeCardView(
                                            establishmentId,
                                            photoUrl,
                                            title,
                                            description,
                                            timestamp
                                        )

                                        listCards.add(homeCardView)

                                    }*/

                                listCards.add(doc.toObject(HomeCardView::class.java))

                            }
                            //Log.i("Test","list card rv: ${listCards.toString()}")
                            binding.progressBarHomeFragment.visibility = View.INVISIBLE
                            val homeAdapter = HomeAdapterEstablishment(listCards)
                            binding.rvHome.adapter = homeAdapter
                        }
                }
            }
    }
}