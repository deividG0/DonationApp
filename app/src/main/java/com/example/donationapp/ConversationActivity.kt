package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ConversationActivity : AppCompatActivity() {

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var textViewNotificationConversation : TextView
    private lateinit var progressBarConversation : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        supportActionBar?.title = "Conversas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewNotificationConversation = findViewById(R.id.textViewNotificationConversation)
        progressBarConversation = findViewById(R.id.progressBarConversation)
        progressBarConversation.visibility = View.VISIBLE

        adapter = GroupAdapter()

        val rv: RecyclerView = findViewById(R.id.conversationRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        fetchLastMessage()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchLastMessage() {

        val id: String = FirebaseAuth.getInstance().uid.toString()

        FirebaseFirestore.getInstance().collection("/last-messages")
            .document(id)
            .collection("contacts")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    Log.i("Test", "collection last-messages not existed")
                    textViewNotificationConversation.text = "Nenhuma conversa foi iniciada."

                } else {
                    FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(id)
                        .collection("contacts")
                        .addSnapshotListener { value, error ->

                            val documentChange: List<DocumentChange> =
                                value?.documentChanges as List<DocumentChange>

                            if (documentChange != null) {
                                for (doc in documentChange) {
                                    if (doc.type == DocumentChange.Type.ADDED) {

                                        val conversation: Conversation =
                                            doc.document.toObject(Conversation::class.java)

                                        adapter.add(ConversationItem(conversation))

                                    }
                                }
                            }
                        }
                }
            }
        progressBarConversation.visibility = View.INVISIBLE
    }

    private inner class ConversationItem(private var conversation: Conversation) :
        Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val username: TextView = viewHolder.itemView.findViewById(R.id.textViewUsernameMessage)
            val message: TextView = viewHolder.itemView.findViewById(R.id.textViewLastMessage)
            val photo: ImageView = viewHolder.itemView.findViewById(R.id.imageViewUserMessage)

            username.text = conversation.username
            message.text = conversation.lastMessage

            viewHolder.itemView.setOnClickListener {

                val intent = Intent(viewHolder.itemView.context, ChatActivity::class.java)
                intent.putExtra("username",conversation.username)
                intent.putExtra("userUrlPhoto",conversation.photoUrl)
                intent.putExtra("toId",conversation.id)
                startActivity(intent)

            }

            Picasso.get()
                .load(conversation.photoUrl)
                .into(photo)

        }

        override fun getLayout(): Int {
            return R.layout.item_conversation
        }
    }

}