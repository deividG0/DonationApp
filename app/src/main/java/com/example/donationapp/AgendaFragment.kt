package com.example.donationapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.donationapp.databinding.FragmentAgendaBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class AgendaFragment : Fragment() {

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var binding : FragmentAgendaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgendaBinding.inflate(inflater,container,false)

        adapter = GroupAdapter()

        val rv: RecyclerView = binding.agendaRecyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        rvTest()

        return binding.root
    }

    private fun rvTest(){

        val agenda = Agenda()

        agenda.establishmentName = "Mercado do João"
        agenda.description = "50 kilos de batata"
        agenda.data = "25"
        agenda.hours = "17:25"

        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))
        adapter.add(AgendaItem(agenda))

    }

    private inner class AgendaItem(private var agenda: Agenda) :
        Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val data: TextView = viewHolder.itemView.findViewById(R.id.textViewDate)
            val establishmentName: TextView = viewHolder.itemView.findViewById(R.id.textViewNameAgenda)
            val description: TextView = viewHolder.itemView.findViewById(R.id.textViewDescriptionAgenda)
            val hours: TextView = viewHolder.itemView.findViewById(R.id.textViewHoursAgenda)

            data.text = agenda.data
            establishmentName.text = agenda.establishmentName
            description.text = agenda.description
            hours.text = agenda.hours

        }

        override fun getLayout(): Int {
            return R.layout.item_agenda
        }
    }

}