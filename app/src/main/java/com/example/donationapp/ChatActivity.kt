package com.example.donationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var toIdEstablishment : String
    private lateinit var establishmentUrlPhoto : String
    private lateinit var establishmentUsername: String
    private lateinit var editTextChat : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        adapter = GroupAdapter()

        val rv : RecyclerView = findViewById(R.id.recyclerViewChat)
        val buttonChat = findViewById<Button>(R.id.buttonChat)

        editTextChat = findViewById(R.id.editTextChat)

        toIdEstablishment = intent.getStringExtra("toId")!!
        establishmentUrlPhoto = intent.getStringExtra("userUrlPhoto")!!
        establishmentUsername = intent.getStringExtra("username")!!

        supportActionBar?.title = establishmentUsername
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.i("Test", "{$toIdEstablishment}")

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        fetchMessages()

        buttonChat.setOnClickListener {

            sendMessage()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun sendMessage() {

        val text : String = editTextChat.text.toString()
        editTextChat.text = null
        val fromId : String = FirebaseAuth.getInstance().uid.toString()
        val toId : String = toIdEstablishment
        val timestamp : Long = System.currentTimeMillis()

        val message = Message()
        message.fromId = fromId
        message.text = text
        message.toId = toId
        message.timestamp = timestamp

        if (message.text!!.isNotEmpty()){
            FirebaseFirestore.getInstance().collection("/conversation")
                .document(fromId)
                .collection(toId)
                .add(message)
                .addOnSuccessListener {

                    Log.d("Test", it.toString())

                    val conversation = Conversation()
                    conversation.id = toId
                    conversation.photoUrl = establishmentUrlPhoto
                    conversation.username = establishmentUsername
                    conversation.timestamp = message.timestamp
                    conversation.lastMessage = message.text

                    findViewById<RecyclerView>(R.id.recyclerViewChat).smoothScrollToPosition(adapter.itemCount)

                    FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(fromId)
                        .collection("contacts")
                        .document(toId)
                        .set(conversation)



                }.addOnFailureListener {

                    Log.e("Test", it.message.toString(),it)

                }

            FirebaseFirestore.getInstance().collection("/conversation")
                .document(toId)
                .collection(fromId)
                .add(message)
                .addOnSuccessListener { it ->

                    Log.d("Test", it.toString())

                    val conversation = Conversation()

                    FirebaseFirestore.getInstance().collection(UniversalCommunication.userType)
                        .document(fromId)
                        .get()
                        .addOnSuccessListener { doc ->

                            conversation.id = fromId
                            conversation.photoUrl = doc.get("photoUrl").toString()
                            conversation.username = doc.get("name").toString()
                            conversation.timestamp = message.timestamp
                            conversation.lastMessage = message.text

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                .document(toId)
                                .collection("contacts")
                                .document(fromId)
                                .set(conversation)

                        }

                }.addOnFailureListener {

                    Log.e("Test", it.message.toString(),it)

                }
        }
    }

    private fun fetchMessages(){

        val currentUserId = FirebaseAuth.getInstance().uid

        FirebaseFirestore.getInstance().collection("conversation")
            .document(currentUserId!!)
            .collection(toIdEstablishment)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->

                Log.i("Test","Listener adicionado")

                val documentChanges : List<DocumentChange> = value?.documentChanges as List<DocumentChange>

                if (documentChanges != null){

                    Log.i("Test","Entrou aqui 1")

                    for (doc in documentChanges){

                        Log.i("Test","Entrou aqui 2")

                        if(doc.type == DocumentChange.Type.ADDED){

                            val message : Message = doc.document.toObject(Message::class.java)
                            Log.i("Test","Entrou aqui 3, texto: ${message.text}")
                            adapter.add(MessageItem(message))

                        }
                    }
                }
            }
    }

    private inner class MessageItem(private var message: Message) : Item<ViewHolder>(){

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val imageMessageUser : ImageView = viewHolder.itemView.findViewById(R.id.imageMessageUser)
            val textViewMessage : TextView = viewHolder.itemView.findViewById(R.id.textViewMessage)

            textViewMessage.text = message.text

            if(message.fromId == FirebaseAuth.getInstance().uid){

                FirebaseFirestore.getInstance().collection(UniversalCommunication.userType)
                    .document(FirebaseAuth.getInstance().uid!!)
                    .get()
                    .addOnSuccessListener {

                        Picasso.get()
                            .load(it.get("photoUrl").toString())
                            .into(imageMessageUser)

                    }

            }else{

                Picasso.get()
                    .load(establishmentUrlPhoto)
                    .into(imageMessageUser)

            }
        }

        override fun getLayout(): Int {
            return if(message.fromId == FirebaseAuth.getInstance().uid){

                R.layout.item_to_message

            }else{

                R.layout.item_from_message

            }
        }
    }

}