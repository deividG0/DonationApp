package com.example.donationapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donationapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

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

        val homeAdapter = HomeAdapter(exampleCardList)

        binding.rvHome.adapter = homeAdapter
        binding.rvHome.layoutManager = LinearLayoutManager(context)
        binding.rvHome.setHasFixedSize(true)

        return binding.root
    }
}