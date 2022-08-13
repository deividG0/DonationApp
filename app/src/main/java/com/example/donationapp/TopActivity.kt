package com.example.donationapp

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging

class TopActivity : AppCompatActivity() {

    //NOTIFICATIONS
    private val CHANNEL_ID = "channel_id_01"
    private val notificationId = 101

    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        val application : DonationApplication = application as DonationApplication
        getApplication().registerActivityLifecycleCallbacks(application)

        progressBar = findViewById(R.id.progressBarTop)
        progressBar.visibility = View.INVISIBLE

        //Inicializando menu inferior e configurando-o com a navegação entre os fragmentos

        bottomNavigation = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigation.setupWithNavController(navController)

        UniversalCommunication.bottomNavigation = bottomNavigation

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
        UniversalCommunication.firstTime = true

    }

    private fun verifyAuthentication() {

        val manualReset = true

        //FirebaseAuth.getInstance().uid == null || FirebaseAuth.getInstance().currentUser == null

        if (FirebaseAuth.getInstance().uid == null || FirebaseAuth.getInstance().currentUser == null) {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }else{

            //verifyUserAccountType()
            UniversalCommunication.createBadgeSolicitation()

        }
    }

    private fun verifyUserAccountType(){

    }
}