package com.example.donationapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class AgendaFragment : Fragment() {

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var binding: FragmentAgendaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgendaBinding.inflate(inflater, container, false)

        adapter = GroupAdapter()

        val rv: RecyclerView = binding.agendaRecyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        rvTest()

        return binding.root
    }

    private fun rvTest() {

        /*val agenda = Agenda()

        agenda.title = "Mercado do João"
        agenda.description = "50 kilos de batata"
        agenda.data = "25"

        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))*/

        fetchAgenda()

    }

    private fun fetchAgenda() {

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("agenda")
            .document(currentUserId!!)
            .collection("dates")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

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

            data.text = agenda.data
            establishmentName.text = agenda.title
            description.text = agenda.description

        }

        override fun getLayout(): Int {
            return R.layout.item_agenda
        }
    }

}