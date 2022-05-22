package com.example.donationapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TopActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        progressBar = findViewById(R.id.progressBarTop)
        progressBar.visibility = View.INVISIBLE

        //Inicializando menu inferior e configurando-o com a navegação entre os fragmentos

        bottomNavigation = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigation.setupWithNavController(navController)

        //Toast.makeText(this, "USER ID: ${FirebaseAuth.getInstance().uid}", Toast.LENGTH_SHORT)
        //    .show()

        Toast.makeText(this, "Login efetuado !", Toast.LENGTH_LONG)
            .show()

        verifyAuthentication()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.logout -> signOut()

        }
        when (item.itemId) {

            R.id.info -> {

                when (bottomNavigation.selectedItemId) {

                    R.id.home -> showInformation(R.string.home_info)
                    R.id.agenda -> showInformation(R.string.agenda_info)
                    R.id.map -> showInformation(R.string.map_info)
                    R.id.profile -> showInformation(R.string.profile_info)

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInformation(informationText : Int){

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.information_dialog)

        dialog.findViewById<TextView>(R.id.textViewInfo).text = resources.getString(informationText)

        dialog.setCancelable(true)

        dialog.show()

    }

    private fun signOut() {

        FirebaseAuth.getInstance().signOut()
        verifyAuthentication()

    }

    private fun verifyAuthentication() {

        if (FirebaseAuth.getInstance().uid == null || FirebaseAuth.getInstance().currentUser == null) {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }else{

            verifyUserAccountType()

        }
    }

    private fun verifyUserAccountType(){

        val currentUserId = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection("establishment")
            .get()
            .addOnSuccessListener {
                for (doc in it){
                    if(doc.toObject(Establishment::class.java).id == currentUserId){

                        UniversalCommunication.userType = "establishment"
                        Log.i("Test","é do tipo estabelecimento")
                        //Toast.makeText(this,"userType: establishment",Toast.LENGTH_SHORT).show()

                    }
                }
            }

        FirebaseFirestore.getInstance().collection("institution")
            .get()
            .addOnSuccessListener {
                for (doc in it){
                    if(doc.toObject(Institution::class.java).id == currentUserId){

                        UniversalCommunication.userType = "institution"
                        //Toast.makeText(this,"userType: institution",Toast.LENGTH_SHORT).show()

                    }
                }
            }

        FirebaseFirestore.getInstance().collection("person")
            .get()
            .addOnSuccessListener {
                for (doc in it){
                    if(doc.toObject(Person::class.java).id == currentUserId){

                        UniversalCommunication.userType = "person"
                        //Toast.makeText(this,"userType: person",Toast.LENGTH_SHORT).show()

                    }
                }
            }
    }
}