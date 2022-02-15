package com.example.donationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donationapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listCards : MutableList<HomeCardView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        listCards = mutableListOf()

        binding.progressBarHomeFragment.visibility = View.VISIBLE

        val defaultPhotoUrl =
            "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"
        val defaultDescriptionCard =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi mi turpis, commodo a accumsan lacinia, tincidunt vel mauris. Sed vehicula hendrerit augue, nec laoreet ligula lacinia porta. Maecenas vehicula tellus vitae mauris luctus vulputate. Sed sit amet interdum sapien. Donec molestie odio in hendrerit faucibus. Curabitur venenatis tempus lorem ut consequat. Nulla sed condimentum massa. Praesent rhoncus odio sit amet urna elementum, quis consequat massa pellentesque. Nam id lacinia ipsum, in pulvinar diam."

        val exampleCardList: MutableList<HomeCardView> = mutableListOf()
        exampleCardList.add(HomeCardView("1",defaultPhotoUrl, "Title one",defaultDescriptionCard))
        exampleCardList.add(HomeCardView("2",defaultPhotoUrl, "Title two",defaultDescriptionCard))
        exampleCardList.add(HomeCardView("3",defaultPhotoUrl, "Title three",defaultDescriptionCard))
        exampleCardList.add(HomeCardView("4",defaultPhotoUrl, "Title four",defaultDescriptionCard))

        binding.rvHome.setHasFixedSize(true)
        binding.rvHome.layoutManager = LinearLayoutManager(context)
        //binding.rvHome.adapter = HomeAdapter(exampleCardList)

        getAssociationType()
        fetchCards()

        /*
        binding.rvHome.adapter = homeAdapter
        binding.rvHome.layoutManager = LinearLayoutManager(context)
        binding.rvHome.setHasFixedSize(true)
        */

        if(arguments!=null) {
            Log.i("Test", "------->   ${arguments?.getString("message")}")
        }

        binding.floatingActionButton.setOnClickListener {

            val intent = Intent(context, CreateHomeCardActivity::class.java)
            startActivity(intent)

        }
        return binding.root

    }
    private fun getAssociationType() {

        FirebaseFirestore.getInstance().collection("/users")
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {

                verifyAssociationType(it.toObject(User::class.java)?.associationId!!)

            }
            .addOnFailureListener {

                Log.e("Test", it.message, it)

            }
    }

    private fun verifyAssociationType(associationId : String) {

        val institutionIdList: MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance().collection("/institution")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    Log.i("Test", "ENTROU INSTITUIÇÃO ${doc.id}")
                    institutionIdList.add(doc.id)
                }

                if (institutionIdList.contains(associationId)) {

                    Log.i("Test", "ESTE ESTA ASSOCIADO A institution        ------------")
                    setupWithAssociationType("institution")

                } else {

                    Log.i("Test", "ESTE ESTA ASSOCIADO A establishment        ------------")
                    setupWithAssociationType("establishment")
                }

            }.addOnFailureListener {

                Log.i("Test", "Não foi possível recuperar todas as instituições do banco de dados")

            }
    }

    private fun setupWithAssociationType(type : String){

        if(type == "institution"){

            binding.floatingActionButton.show()
            Log.i("Test","CHEGUEI ATÉ AQUI institution")

        }else{

            binding.floatingActionButton.hide()
            Log.i("Test","CHEGUEI ATÉ AQUI establishment")

        }
    }

    private fun fetchCards(){

        FirebaseFirestore.getInstance().collection("requirements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                for(doc in it){

                    listCards.add(doc.toObject(HomeCardView::class.java))

                }
                Log.i("Test","list card rv: ${listCards.toString()}")
                binding.progressBarHomeFragment.visibility = View.INVISIBLE
                val homeAdapter = HomeAdapter(listCards)
                binding.rvHome.adapter = homeAdapter
            }
    }
}