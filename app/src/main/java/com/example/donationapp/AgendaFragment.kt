package com.example.donationapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.donationapp.databinding.FragmentAgendaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*

class AgendaFragment : Fragment() {

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var binding: FragmentAgendaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgendaBinding.inflate(inflater, container, false)

        binding.progressBarAgendaFragment.visibility = View.VISIBLE

        adapter = GroupAdapter()

        val rv: RecyclerView = binding.agendaRecyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        fetchAgenda()

        return binding.root
    }

    private fun fetchAgenda() {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("agenda")
            .document(currentUserId!!)
            .collection("dates")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    binding.progressBarAgendaFragment.visibility = View.INVISIBLE
                    binding.textViewAgenda.text = "Nenhuma entrega/busca marcada."
                    Log.i("Test", "collection agenda not existed")

                } else {

                    FirebaseFirestore.getInstance().collection("agenda")
                        .document(currentUserId!!)
                        .collection("dates")
                        .addSnapshotListener { value, error ->

                            Log.i("Test", "Listener agenda added")

                            val documentChanges: List<DocumentChange> =
                                value?.documentChanges as List<DocumentChange>

                            if (documentChanges != null) {
                                for (doc in documentChanges) {
                                    if (doc.type == DocumentChange.Type.ADDED) {

                                        val agenda: Agenda =
                                            doc.document.toObject(Agenda::class.java)

                                        adapter.add(AgendaItem(agenda))

                                    }
                                }
                            }
                            binding.progressBarAgendaFragment.visibility = View.INVISIBLE
                        }
                }
            }
    }

    private inner class AgendaItem(private var agenda: Agenda) :
        Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val data: TextView = viewHolder.itemView.findViewById(R.id.textViewDate)
            val establishmentName: TextView =
                viewHolder.itemView.findViewById(R.id.textViewNameAgenda)
            val description: TextView =
                viewHolder.itemView.findViewById(R.id.textViewDescriptionAgenda)
            val buttonDeleteAgendaItem = viewHolder.itemView.findViewById<ImageButton>(R.id.buttonDeleteAgendaItem)
            //buttonDeleteAgendaItem.visibility = View.INVISIBLE

            buttonDeleteAgendaItem.setOnClickListener {
                deleteAgendaItem(agenda)
                adapter.removeGroup(position)
                adapter.notifyDataSetChanged()
            }

            val date = SimpleDateFormat("dd/MM/yyyy").parse(agenda.data!!)

            // Timestamp 7 days = 604800000L

            val today = Date() //SimpleDateFormat("dd/MM/yyyy").format(Date())
            Log.i("Test","date from the agenda item: $date, com .time: ${date.time}")
            Log.i("Test","today: $today, com .time: ${today.time}")

            val nestWeek = today.time + 604800000L

            if(date.time <= nestWeek){

                viewHolder.itemView.findViewById<View>(R.id.agendaType).setBackgroundResource(R.drawable.bg_item_agenda_rounded_red)

            }else{

                viewHolder.itemView.findViewById<View>(R.id.agendaType).setBackgroundResource(R.drawable.bg_item_agenda_rounded_white)

            }
            if(date.time < today.time){

                Log.i("Test","ta entrando aqui ?")
                viewHolder.itemView.findViewById<View>(R.id.agendaType).setBackgroundResource(R.drawable.bg_item_agenda_rounded_gray)
                buttonDeleteAgendaItem.visibility = View.VISIBLE

            }
            if (agenda.relatedCardId==null){

                viewHolder.itemView.findViewById<View>(R.id.agendaType).setBackgroundResource(R.drawable.bg_item_agenda_rounded_green)

            }

            buttonDeleteAgendaItem.visibility = View.INVISIBLE
            data.text = agenda.data
            establishmentName.text = agenda.title
            description.text = agenda.description

        }

        fun deleteAgendaItem(agenda: Agenda){

            val userId = FirebaseAuth.getInstance().uid

            FirebaseFirestore.getInstance().collection("agenda")
                .document(userId!!)
                .collection("dates")
                .get()
                .addOnSuccessListener {
                    for(doc in it){
                        if (doc["relatedCardId"] == agenda.relatedCardId){
                            FirebaseFirestore.getInstance().collection("agenda")
                                .document(userId!!)
                                .collection("dates")
                                .document(doc.id)
                                .delete()
                            Log.i("Test", "Agenda Item Card Deleted")
                            return@addOnSuccessListener
                        }
                    }
                }

        }

        override fun getLayout(): Int {
            return R.layout.item_agenda
        }
    }

}