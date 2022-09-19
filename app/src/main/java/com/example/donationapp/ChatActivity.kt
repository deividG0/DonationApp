package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        adapter = GroupAdapter()
        loadingDialog = LoadingDialog(this)

        val rv : RecyclerView = findViewById(R.id.recyclerViewChat)
        val buttonChat = findViewById<Button>(R.id.buttonChat)

        editTextChat = findViewById(R.id.editTextChat)

        toIdEstablishment = intent.getStringExtra("toId")!!
        establishmentUrlPhoto = intent.getStringExtra("userUrlPhoto")!!
        establishmentUsername = intent.getStringExtra("username")!!

        supportActionBar?.title = establishmentUsername
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        fetchMessages()

        buttonChat.setOnClickListener {

            sendMessage()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu_chat, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.buttonDeleteMessages -> deleteAllMessages()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllMessages() {

        val currentUserId = FirebaseAuth.getInstance().uid

        MaterialAlertDialogBuilder(this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons)
            .setMessage(resources.getString(R.string.confirmDeleteAllMessages))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.confirm)){ dialog, which ->

                loadingDialog.startLoadingDialog()

                FirebaseFirestore.getInstance().collection("conversation")
                    .document(currentUserId!!)
                    .collection(toIdEstablishment)
                    .get()
                    .addOnSuccessListener {
                        for (doc in it){
                            FirebaseFirestore.getInstance().collection("conversation")
                                .document(currentUserId!!)
                                .collection(toIdEstablishment)
                                .document(doc.id)
                                .delete()
                        }
                        deleteTheLastMessage(currentUserId,toIdEstablishment)
                        loadingDialog.dismissDialog()
                        adapter.clear()
                        Log.i("Test","Deleting all messages ...")
                    }

            }
            .show()

    }

    private fun deleteTheLastMessage(currentUserId: String, toIdEstablishment: String) {

       FirebaseFirestore.getInstance().collection("last-messages")
           .document(currentUserId!!)
           .collection("contacts")
           .document(toIdEstablishment)
           .delete()
           .addOnSuccessListener {
               val intent = Intent(this,TopActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
               startActivity(intent)
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

                            UniversalCommunication.createChatNotification(toId, fromId, doc.get("name").toString(), timestamp, text)

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

                val documentChanges : List<DocumentChange> = value?.documentChanges as List<DocumentChange>

                if (documentChanges != null){

                    for (doc in documentChanges){

                        if(doc.type == DocumentChange.Type.ADDED){

                            val message : Message = doc.document.toObject(Message::class.java)
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