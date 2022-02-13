package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.LogPrinter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        /*

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("message", getAssociationType())
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        fragmentTransaction.replace(R.id.fragmentContainerView, homeFragment).commit()

         */

        //Inicializando menu inferior e configurando-o com a navegação entre os fragmentos

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigation.setupWithNavController(navController)

        Toast.makeText(this, "USER ID: ${FirebaseAuth.getInstance().uid}", Toast.LENGTH_SHORT)
            .show()

        verifyAuthentication()
        getAssociationType()

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

    private fun getAssociationType(){

        val associationId = getAssociationId()
        Log.i("Test", "ASSOCIATION ID in function ------------------> $associationId")
        var type: String = ""

        if (getListInstitutionsId().contains(associationId)) {

            Log.i("Test", "institution        ------------")
            type = "institution"
            Toast.makeText(this, "ASSOCIATION TYPE ----> ${type}", Toast.LENGTH_SHORT)
                .show()

        } else if (getListEstablishmentsId().contains(associationId)) {

            Log.i("Test", "establishment        ------------")
            type = "establishment"
            Toast.makeText(this, "ASSOCIATION TYPE ----> ${type}", Toast.LENGTH_SHORT)
                .show()

        }

    }

    private fun getAssociationId(): String {

        var associationId: String = ""

        FirebaseFirestore.getInstance().collection("/users")
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {

                associationId = it.toObject(User::class.java)?.associationId!!
                Log.i("Test", "ASSOCIATION ID ------------------> $associationId")

            }
            .addOnFailureListener {

                Log.e("Test", it.message, it)

            }

        return associationId
    }

    private fun getListInstitutionsId(): MutableList<String> {

        val institutionIdList: MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance().collection("/institution")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    Log.i("Test", "ENTROU INSTITUIÇÃO ${doc.id}")
                    institutionIdList.add(doc.id)
                }
            }.addOnFailureListener {

                Log.i("Test", "Não foi possível recuperar todas as instituições do banco de dados")

            }

        return institutionIdList

    }

    private fun getListEstablishmentsId(): MutableList<String> {

        val establishmentIdList: MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance().collection("/establishment")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    Log.i("Test", "ENTROU ESTABLISHMENT ${doc.id}")
                    establishmentIdList.add(doc.id)
                }
            }.addOnFailureListener {

                Log.i("Test", "Não foi possível recuperar todas as instituições do banco de dados")

            }

        return establishmentIdList

    }
}