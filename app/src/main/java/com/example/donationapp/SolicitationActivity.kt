package com.example.donationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donationapp.databinding.ActivitySolicitationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.awaitAll

class SolicitationActivity : AppCompatActivity() {

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var binding: ActivitySolicitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySolicitationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressBarSolicitationFragment.visibility = View.VISIBLE

        adapter = GroupAdapter()

        supportActionBar?.title = "Solicitações"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerViewSolicitation.adapter = adapter
        binding.recyclerViewSolicitation.layoutManager = LinearLayoutManager(this)

        verifyOfferExistence()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun verifyOfferExistence() {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(currentUserId!!)
            .collection("solicitations")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    binding.progressBarSolicitationFragment.visibility = View.INVISIBLE
                    binding.textViewSolicitation.text = "Nenhuma solicitação emitida."
                    Log.i("Test", "collection solicitation not existed")
                    adapter.clear()

                } else {
                    FirebaseFirestore.getInstance().collection("solicitation")
                        .document(currentUserId!!)
                        .collection("solicitations")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { solicitations ->
                            for (doc in solicitations) {
                                FirebaseFirestore.getInstance().collection("offer")
                                    .whereEqualTo(
                                        "id",
                                        doc.toObject(Solicitation::class.java).relatedCardId
                                    )
                                    .get()
                                    .addOnSuccessListener { offers ->
                                        if (offers.isEmpty && doc.toObject(Solicitation::class.java).relatedCardId != null) {

                                            FirebaseFirestore.getInstance()
                                                .collection("solicitation")
                                                .document(currentUserId!!)
                                                .collection("solicitations")
                                                .document(doc.get("id").toString())
                                                .delete()

                                        } else {
                                            if (UniversalCommunication.userType == "establishment") {

                                                val solicitation: Solicitation =
                                                    doc.toObject(Solicitation::class.java)
                                                adapter.add(
                                                    SolicitationItemEstablishment(
                                                        solicitation
                                                    )
                                                )

                                            } else {

                                                val solicitation: Solicitation =
                                                    doc.toObject(Solicitation::class.java)
                                                adapter.add(SolicitationItem(solicitation))
                                            }
                                        }
                                    }
                            }
                            binding.progressBarSolicitationFragment.visibility = View.INVISIBLE
                        }
                }
            }
    }

    private fun fetchSolicitations() {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(currentUserId!!)
            .collection("solicitations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    if (UniversalCommunication.userType == "establishment") {

                        val solicitation: Solicitation =
                            doc.toObject(Solicitation::class.java)
                        adapter.add(
                            SolicitationItemEstablishment(
                                solicitation
                            )
                        )

                    } else {

                        val solicitation: Solicitation =
                            doc.toObject(Solicitation::class.java)
                        adapter.add(SolicitationItem(solicitation))
                    }
                    Log.i("Test", "Rodou uma vez")
                }
                binding.progressBarSolicitationFragment.visibility = View.INVISIBLE
            }
    }

    private fun createAgendaItem(
        relatedCardId: String?,
        id: String?,
        data: String?,
        titleEstablishment: String?,
        description: String?,
        fromId: String?
    ) {

        val agenda = Agenda()
        agenda.title = titleEstablishment
        agenda.data = data
        agenda.description = description
        agenda.relatedCardId = relatedCardId

        var title: String?

        FirebaseFirestore.getInstance().collection("establishment")
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {

                FirebaseFirestore.getInstance().collection("agenda")
                    .document(FirebaseAuth.getInstance().uid!!)
                    .collection("dates")
                    .add(agenda)

                title = it.get("name").toString()

                val agenda2 = Agenda()
                agenda2.title = title
                agenda2.data = data
                agenda2.description = description
                agenda2.relatedCardId = relatedCardId

                FirebaseFirestore.getInstance().collection("agenda")
                    .document(fromId!!)
                    .collection("dates")
                    .add(agenda2)

                deleteSolicitation(id, fromId)
                if (relatedCardId != null) {
                    deleteCard(relatedCardId!!)
                }
            }
    }

    private fun deleteSolicitation(id: String?, fromId: String?) {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(fromId!!)
            .collection("solicitations")
            .document(id!!)
            .delete()
            .addOnSuccessListener {

                Log.i("Test", "Solicitation deleted ! 1")

            }.addOnFailureListener {

                Log.e("Test", "Error deleting document", it)

            }

        FirebaseFirestore.getInstance().collection("solicitation")
            .document(currentUserId!!)
            .collection("solicitations")
            .document(id!!)
            .delete()
            .addOnSuccessListener {

                Log.i("Test", "Solicitation deleted ! 2")
                //fetchSolicitations()

            }.addOnFailureListener {

                Log.e("Test", "Error deleting document", it)

            }
    }

    private fun deleteCard(relatedCardId: String) {

        FirebaseFirestore.getInstance().collection("offer")
            .document(relatedCardId)
            .delete()
            .addOnSuccessListener {

                Log.i("Test", "Card deleted !")

            }
    }

    private inner class SolicitationItem(private var solicitation: Solicitation) :
        Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val photoSolicitation: ImageView =
                viewHolder.itemView.findViewById(R.id.photoSolicitation)
            val titleSolicitation: TextView =
                viewHolder.itemView.findViewById(R.id.titleSolicitation)
            val descriptionSolicitation: TextView =
                viewHolder.itemView.findViewById(R.id.descriptionSolicitation)
            val dateSolicitation: TextView = viewHolder.itemView.findViewById(R.id.dateSolicitation)
            val statusSolicitation: TextView =
                viewHolder.itemView.findViewById(R.id.statusSolicitation)

            if (solicitation.relatedCardId == null) {

                viewHolder.itemView.findViewById<View>(R.id.solicitationType)
                    .setBackgroundResource(R.drawable.bg_item_agenda_rounded_green)

            }

            FirebaseFirestore.getInstance().collection("establishment")
                .document(solicitation.toId!!)
                .get()
                .addOnSuccessListener {

                    Picasso.get()
                        .load(it.get("photoUrl").toString())
                        .into(photoSolicitation)

                    titleSolicitation.text = it.get("name").toString()

                }

            descriptionSolicitation.text = solicitation.description
            dateSolicitation.text = solicitation.date

            if (solicitation.status == "pending")
                statusSolicitation.text = "Pendente"

        }

        override fun getLayout(): Int {

            return R.layout.item_solicitation

        }
    }

    private inner class SolicitationItemEstablishment(private var solicitation: Solicitation) :
        Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val photoSolicitation: ImageView =
                viewHolder.itemView.findViewById(R.id.photoSolicitation)
            val titleSolicitation: TextView =
                viewHolder.itemView.findViewById(R.id.titleSolicitation)
            val descriptionSolicitation: TextView =
                viewHolder.itemView.findViewById(R.id.descriptionSolicitation)
            val dateSolicitation: TextView = viewHolder.itemView.findViewById(R.id.dateSolicitation)

            val buttonSolicitationAccept: Button =
                viewHolder.itemView.findViewById(R.id.buttonSolicitationAccept)
            val buttonSolicitationDecline: Button =
                viewHolder.itemView.findViewById(R.id.buttonSolicitationDecline)

            if (solicitation.relatedCardId == null) {

                viewHolder.itemView.findViewById<View>(R.id.solicitationType)
                    .setBackgroundResource(R.drawable.bg_item_agenda_rounded_green)

            }

            buttonSolicitationAccept.setOnClickListener {

                MaterialAlertDialogBuilder(viewHolder.itemView.context)
                    .setView(R.layout.solicitation_dialog_title)
                    //.setTitle("A oferta referente a está solicitação será excluída. Deseja continuar ?")
                    //.setTitle(resources.getString(R.string.solicitationAcceptConfirmation))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    }
                    .setPositiveButton(resources.getString(R.string.proceed)) { dialog, which ->

                        createAgendaItem(
                            solicitation.relatedCardId,
                            solicitation.id,
                            solicitation.date,
                            solicitation.title,
                            solicitation.description,
                            solicitation.fromId
                        )

                        Toast.makeText(
                            viewHolder.itemView.context,
                            "Solicitação aceita !",
                            Toast.LENGTH_LONG
                        )
                            .show()

                        adapter.remove(viewHolder.item)
                        adapter.notifyDataSetChanged()

                    }
                    .show()

            }

            buttonSolicitationDecline.setOnClickListener {

                MaterialAlertDialogBuilder(viewHolder.itemView.context)
                    .setTitle(resources.getString(R.string.solicitationDeclineConfirmation))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    }
                    .setPositiveButton(resources.getString(R.string.proceed)) { dialog, which ->

                        deleteSolicitation(solicitation.id, solicitation.fromId)
                        Log.i("Test", "id 1: ${solicitation.id} e id 2: ${solicitation.fromId}")
                        Toast.makeText(viewHolder.itemView.context, "DELETOU", Toast.LENGTH_LONG)
                            .show()
                        adapter.remove(viewHolder.item)
                        adapter.notifyDataSetChanged()

                    }
                    .show()

            }

            FirebaseFirestore.getInstance().collection("institution")
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.get("id") == solicitation.fromId) {

                            Picasso.get()
                                .load(doc.get("photoUrl").toString())
                                .into(photoSolicitation)

                            titleSolicitation.text = doc.get("name").toString()
                            return@addOnSuccessListener

                        }
                    }
                    FirebaseFirestore.getInstance().collection("person")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (doc in documents) {
                                if (doc.get("id") == solicitation.fromId) {

                                    Picasso.get()
                                        .load(doc.get("photoUrl").toString())
                                        .into(photoSolicitation)

                                    titleSolicitation.text = doc.get("name").toString()
                                    return@addOnSuccessListener

                                }
                            }
                        }
                }

            descriptionSolicitation.text = solicitation.description
            dateSolicitation.text = solicitation.date

        }

        override fun getLayout(): Int {

            return R.layout.item_solicitation_establishment

        }
    }

}