package com.example.donationapp

import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i("Test","OIA A MESSAge ${this.arguments?.getString("message")}")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val defaultPhotoUrl =
            "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/default-group-icon.png?alt=media&token=9599ca53-56b1-49c0-81c0-f0ac4639a60c"
        val defaultDescriptionCard =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi mi turpis, commodo a accumsan lacinia, tincidunt vel mauris. Sed vehicula hendrerit augue, nec laoreet ligula lacinia porta. Maecenas vehicula tellus vitae mauris luctus vulputate. Sed sit amet interdum sapien. Donec molestie odio in hendrerit faucibus. Curabitur venenatis tempus lorem ut consequat. Nulla sed condimentum massa. Praesent rhoncus odio sit amet urna elementum, quis consequat massa pellentesque. Nam id lacinia ipsum, in pulvinar diam."

        val exampleCardList: MutableList<HomeCardView> = mutableListOf()
        exampleCardList.add(HomeCardView(defaultPhotoUrl, "Title one",defaultDescriptionCard))
        exampleCardList.add(HomeCardView(defaultPhotoUrl, "Title two",defaultDescriptionCard))
        exampleCardList.add(HomeCardView(defaultPhotoUrl, "Title three",defaultDescriptionCard))
        exampleCardList.add(HomeCardView(defaultPhotoUrl, "Title four",defaultDescriptionCard))

        //Toast.makeText(context,"${this.arguments?.getString("message")}",Toast.LENGTH_LONG).show()
        //Toast.makeText(context,"${getListAssociationsId()}",Toast.LENGTH_LONG).show()



        val homeAdapter = HomeAdapter(exampleCardList)

        binding.rvHome.adapter = homeAdapter
        binding.rvHome.layoutManager = LinearLayoutManager(context)
        binding.rvHome.setHasFixedSize(true)

        binding.floatingActionButton.setOnClickListener {

            val intent = Intent(context, CreateHomeCardActivity::class.java)
            startActivity(intent)

        }
        return binding.root

    }
}