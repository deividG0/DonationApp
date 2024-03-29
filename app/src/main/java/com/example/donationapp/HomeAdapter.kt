package com.example.donationapp

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class HomeAdapter(private val cardList: List<HomeCardView>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_home, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentCard = cardList[position]
        val photoImageView = holder.itemView.findViewById<ImageView>(R.id.logoImageCard)

        Picasso.get()
            .load(currentCard.photoUrl)
            .into(photoImageView)

        holder.itemView.findViewById<TextView>(R.id.textViewCardTitle).text = currentCard.title
        holder.itemView.findViewById<TextView>(R.id.textViewDescriptionCard).text =
            currentCard.description

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss")
        val netDate = Date(currentCard.timestamp!!)
        val date = simpleDateFormat.format(netDate)
        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.progress = currentCard.progress!!

        holder.itemView.findViewById<TextView>(R.id.textViewDateCard).text = date

        holder.itemView.findViewById<Button>(R.id.buttonSendMessage).setOnClickListener {

            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("username", currentCard.title)
            intent.putExtra("userUrlPhoto", currentCard.photoUrl)
            intent.putExtra("toId", currentCard.establishmentId)
            startActivity(holder.itemView.context, intent, null)

        }

        holder.itemView.findViewById<TextView>(R.id.buttonAccessProfile).setOnClickListener {

            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            intent.putExtra("toId", currentCard.establishmentId)
            startActivity(holder.itemView.context, intent, null)

        }

        holder.itemView.findViewById<TextView>(R.id.buttonRequest).setOnClickListener {

            val currentId = FirebaseAuth.getInstance().uid!!
            val dialog = Dialog(holder.itemView.context!!)

            dialog.setContentView(R.layout.solicitation_dialog_input)

            val calendarView = dialog.findViewById<CalendarView>(R.id.calendarViewSolicitation)
            val cancelButtonDialog =
                dialog.findViewById<Button>(R.id.cancelButtonSolicitationDialog)
            val confirmButtonDialog =
                dialog.findViewById<Button>(R.id.confirmButtonSolicitationDialog)

            val sdf = SimpleDateFormat("dd/MM/yyyy")
            var date : String = sdf.format(calendarView.date)
            Log.i("Test",date)

            calendarView.minDate = System.currentTimeMillis() - 1000

            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

                Toast.makeText(
                    holder.itemView.context,
                    "Data: $dayOfMonth/${month + 1}/$year",
                    Toast.LENGTH_LONG
                ).show()

                date = "$dayOfMonth/${month + 1}/$year"

                //date = "$dayOfMonth/${month + 1}/$year"

            }

            confirmButtonDialog.setOnClickListener {

                FirebaseFirestore.getInstance().collection("solicitation")
                    .document(currentId)
                    .collection("solicitations")
                    .whereEqualTo("relatedCardId", currentCard.id)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty){
                            UniversalCommunication.createSolicitation(
                                currentCard.id,
                                currentCard.title,
                                currentCard.photoUrl,
                                currentCard.description,
                                date,
                                currentId,
                                currentCard.establishmentId!!
                            )

                            Toast.makeText(
                                holder.itemView.context!!,
                                "A solicitação foi enviada.",
                                Toast.LENGTH_LONG
                            ).show()

                        } else{

                            Toast.makeText(
                                holder.itemView.context!!,
                                "Você já solicitou essa oferta.",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }

                dialog.dismiss()

            }

            cancelButtonDialog.setOnClickListener { dialog.cancel() }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}