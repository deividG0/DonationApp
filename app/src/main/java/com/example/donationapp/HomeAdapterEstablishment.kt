package com.example.donationapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapterEstablishment(private val cardList : List<HomeCardView>) : RecyclerView.Adapter<HomeAdapterEstablishment.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_home_establishment,parent,false)
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

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss")
        val netDate = Date(currentCard.timestamp!!)
        val date = simpleDateFormat.format(netDate)
        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.progress = currentCard.progress!!

        holder.itemView.findViewById<TextView>(R.id.textViewDateCard).text = date

    }

    override fun getItemCount(): Int {
        return cardList.size
    }

}