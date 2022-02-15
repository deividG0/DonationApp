package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TopActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        progressBar = findViewById(R.id.progressBarTop)
        progressBar.visibility = View.VISIBLE

        //Inicializando menu inferior e configurando-o com a navegação entre os fragmentos

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigation.setupWithNavController(navController)

        /*

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("message", getAssociationType())
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        fragmentTransaction.replace(R.id.fragmentContainerView, homeFragment).commit()

         */

        Toast.makeText(this, "USER ID: ${FirebaseAuth.getInstance().uid}", Toast.LENGTH_SHORT)
            .show()

        verifyAuthentication()

    }

    private fun verifyFirstLogin() {

        val idCurrentUser = FirebaseAuth.getInstance().uid.toString()

        var user: User

        FirebaseFirestore.getInstance().collection("/users")
            .document(idCurrentUser)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Test", "DocumentSnapshot dado: ${document.data}")
                } else {
                    Log.d("Test", "Documento não encontrado")
                }

                user = document.toObject(User::class.java)!!

                if (user.associationId == null) {

                    val intent = Intent(this, SelectionActivity::class.java)

                    //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(intent)

                }else{
                    progressBar.visibility = View.INVISIBLE

                }

            }
            .addOnFailureListener { exception ->
                Log.d("Test", "Erro", exception)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.logout -> signOut()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {

        FirebaseAuth.getInstance().signOut()
        verifyAuthentication()

    }

    private fun verifyAuthentication() {

        if (FirebaseAuth.getInstance().uid == null) {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        } else {

            verifyFirstLogin()

        }
    }
}