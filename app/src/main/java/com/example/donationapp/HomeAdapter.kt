package com.example.donationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HomeAdapter(private val cardList : List<HomeCardView>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_home,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentCard = cardList[position]
        val photoImageView = holder.itemView.findViewById<ImageView>(R.id.logoImageCard)

        Picasso.get()
            .load(currentCard.photoUrl)
            .into(photoImageView)

        holder.itemView.findViewById<TextView>(R.id.textViewCardTitle).text = currentCard.title
        holder.itemView.findViewById<TextView>(R.id.textViewDescriptionCard).text = currentCard.description

    }

    override fun getItemCount(): Int {
        return cardList.size
    }

}