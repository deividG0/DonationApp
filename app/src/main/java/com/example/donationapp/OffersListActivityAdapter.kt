package com.example.donationapp

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class OffersListActivityAdapter(private val cardList: List<HomeCardView>) :
    RecyclerView.Adapter<OffersListActivityAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_my_offers_list, parent, false)
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

        holder.itemView.findViewById<TextView>(R.id.textViewDateCard).text = date

        holder.itemView.findViewById<Button>(R.id.buttonDeleteCard).setOnClickListener {

            //Pop-up de alerta
            MaterialAlertDialogBuilder(
                holder.itemView.context,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
            )
                .setMessage(holder.itemView.context.resources.getString(R.string.confirmDeleteOffer))
                .setNeutralButton(holder.itemView.context.resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(holder.itemView.context.resources.getString(R.string.confirm)) { dialog, which ->
                    FirebaseFirestore.getInstance().collection("offer")
                        .document(currentCard.id!!)
                        .delete()
                        .addOnSuccessListener {

                            Toast.makeText(
                                holder.itemView.context,
                                "Oferta deletada com sucesso.",
                                Toast.LENGTH_LONG
                            ).show()
                            notifyItemRemoved(position)
                            Log.i("Test", "Card deleted !")

                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                holder.itemView.context,
                                "Erro ao deletar card: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                .show()

        }

        holder.itemView.findViewById<TextView>(R.id.buttonEditCard).setOnClickListener {

            //val currentId = FirebaseAuth.getInstance().uid!!
            val dialog = Dialog(holder.itemView.context!!)

            dialog.setContentView(R.layout.update_offer_dialog)

            val cancelButtonDialog =
                dialog.findViewById<Button>(R.id.cancelButtonSolicitationDialog)
            val confirmButtonDialog =
                dialog.findViewById<Button>(R.id.confirmButtonSolicitationDialog)

            val minusButton = dialog.findViewById<Button>(R.id.minusButton)
            val plusButton = dialog.findViewById<Button>(R.id.plusButton)
            val percentageButton = dialog.findViewById<Button>(R.id.percentageButton)
            val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
            val editTextCardDescription =
                dialog.findViewById<TextInputLayout>(R.id.editTextCardDescription)

            editTextCardDescription.hint = currentCard.description
            progressBar.progress = currentCard.progress!!

            percentageButton.text = progressBar.progress.toString()

            minusButton.setOnClickListener {

                if (progressBar.progress > 0) {
                    percentageButton.text = (progressBar.progress - 10).toString()
                    progressBar.progress = progressBar.progress - 10
                }

            }

            plusButton.setOnClickListener {

                if (progressBar.progress < 100) {
                    percentageButton.text = (progressBar.progress + 10).toString()
                    progressBar.progress = progressBar.progress + 10
                }

            }

            confirmButtonDialog.setOnClickListener {

                updateOffer(
                    currentCard.id!!,
                    editTextCardDescription.editText?.text.toString(),
                    currentCard.description!!,
                    progressBar.progress,
                    currentCard.progress!!,
                    holder.itemView.context
                )
                dialog.dismiss()

            }

            cancelButtonDialog.setOnClickListener { dialog.cancel() }

            dialog.show()

        }
    }

    private fun updateOffer(
        offerId: String,
        newDescription: String,
        oldDesc: String,
        newProgress: Int,
        oldProgress: Int,
        context: Context
    ) {

        var updates: HashMap<String, Any>

        if (newDescription.isNullOrBlank() || newDescription == oldDesc) {
            updates = if (newProgress == oldProgress) {
                hashMapOf(
                    "progress" to oldProgress
                )
            } else {
                hashMapOf(
                    "progress" to newProgress
                )
            }
            FirebaseFirestore.getInstance().collection("offer")
                .document(offerId)
                .update(updates as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Edição realizada com sucesso !",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            updates = if (newProgress == oldProgress) {
                hashMapOf(
                    "description" to newDescription,
                    "progress" to oldProgress
                )
            } else {
                hashMapOf(
                    "description" to newDescription,
                    "progress" to newProgress
                )
            }
            FirebaseFirestore.getInstance().collection("offer")
                .document(offerId)
                .update(updates as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Edição realizada com sucesso !",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}