package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var buttonRegister: Button
    private lateinit var editTextName: TextInputLayout
    private lateinit var editTextEmail: TextInputLayout
    private lateinit var editTextPassword: TextInputLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var autoCompleteTextType: TextInputLayout
    private lateinit var editTextPhone: TextInputLayout

    private val defaultProfileImageUrl = UniversalCommunication.defaultProfileImageUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = "Cadastre-se"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Configurando barra de loading
        setProgressBar()

        //Apresentando opções no dropdown de tipo de conta
        autoCompleteTextType = findViewById(R.id.typeUser)
        val items = listOf("Estabelecimento", "Instituição", "Pessoa física")
        val adapter = ArrayAdapter(this, R.layout.list_item_selection, items)
        (autoCompleteTextType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //Inicializando labels de EditText
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextName = findViewById(R.id.editTextName)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPhone = findViewById(R.id.editTextPhone)

        //Status de erro nulo
        editTextEmail.error = null
        editTextName.error = null
        editTextPassword.error = null

        //Inicializando e ouvindo clique no botão de cadastro
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonRegister.setOnClickListener {

            createUser()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setProgressBar() {

        //Esta função cria a indicação de carregamento ao usuário clicar em "Cadastrar"

        progressBar = findViewById(R.id.progressBarRegister)
        progressBar.visibility = View.INVISIBLE

    }

    private fun createUser() {

        //Esta função cria um usuário com email e senha no Firebase Authentication, antes disso, verifica se todos os campos de cadastro
        //estão preenchidos corretamente.

        //Após a criação no sistema de autenticação do firebase podemos salvar o usuário no banco de dados com o método saveUserInDatabase()

        val name = editTextName.editText?.text.toString()
        val email = editTextEmail.editText?.text.toString()
        val password = editTextPassword.editText?.text.toString()
        val type = autoCompleteTextType.editText?.text.toString()

        //Toast.makeText(
        //    this,
        //    "name: $name, email: $email, password: $password, password length: ${password.length}",
        //    Toast.LENGTH_LONG
        //).show()

        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || type.isEmpty()) {

            if (email.isEmpty()) {
                editTextEmail.error = "Este campo está em branco."
            } else {
                editTextEmail.error = null
            }
            if (name.isEmpty()) {
                editTextName.error = "Este campo está em branco."
            } else {
                editTextName.error = null
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Este campo está em branco."
            } else {
                editTextPassword.error = null
            }
            if (type.isEmpty()) {
                autoCompleteTextType.error = "Este campo está em branco."
            } else {
                autoCompleteTextType.error = null
            }
            return
        }

        if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())){

            editTextEmail.error = "Este email está mal formatado."
            return

        }else{
            editTextEmail.error = null
        }

        if (password.length < 6) {
            editTextPassword.error = "A senha deve conter pelo menos 6 caracteres."
            return
        } else {
            editTextPassword.error = null
        }

        /*
        if(verifyEmailDuality(email)){

            return

        }

         */

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("Test", it.result.user!!.uid)

                    //Fazendo barra de login ficar visível
                    progressBar.visibility = View.VISIBLE

                    //Impossibilitando toque na tela pelo usuário
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )

                    saveUserInDatabase()
                }

            }.addOnFailureListener {

                Log.e("Test", it.message.toString(), it)

            }
    }

    private fun verifyEmailDuality(email: String): Boolean {

        /*

        var registered = false

        FirebaseFirestore.getInstance().collection("/users")
            .get()
            .addOnSuccessListener {
                for (doc in it){
                    if (doc.toObject(User::class.java).email == email){

                        editTextEmail.error = "Este email já está registrado."
                        registered = true
                        break

                    }
                }
            }

         */
        return false

    }

    private fun saveUserInDatabase() {

        val id = FirebaseAuth.getInstance().uid
        val name = editTextName.editText?.text.toString()
        val email = editTextEmail.editText?.text.toString()
        val type = autoCompleteTextType.editText?.text.toString()
        val phone = editTextPhone.editText?.text.toString()

        when (type) {

            "Estabelecimento" -> createEstablishment(id, name, email,phone)
            "Instituição" -> createInstitution(id, name, email,phone)
            "Pessoa física" -> createPerson(id, name, email,phone)

        }

    }

    private fun createEstablishment(id: String?, name: String, email: String, phone: String) {

        val establishment =
            Establishment(id, name, email, defaultProfileImageUrl, null, Latlng(), phone)

        FirebaseFirestore.getInstance().collection("establishment")
            .document(id.toString())
            .set(establishment)
            .addOnSuccessListener {

                val intent = Intent(this, TopActivity::class.java)

                //Permitindo toque na tela novamente
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                //Desativando a barra de loading
                progressBar.visibility = View.INVISIBLE

                //Iniciando nova atividade
                startActivity(intent)


            }.addOnFailureListener {

                Toast.makeText(this, "Ocorreu um erro durante o cadastro.", Toast.LENGTH_LONG)
                    .show()
                Log.e("Test", it.message.toString(), it)

            }
    }

    private fun createInstitution(id: String?, name: String, email: String, phone: String) {

        val institution =
            Institution(id, name, email, defaultProfileImageUrl, null, null,Latlng(), phone)

        FirebaseFirestore.getInstance().collection("institution")
            .document(id.toString())
            .set(institution)
            .addOnSuccessListener {

                val intent = Intent(this, TopActivity::class.java)

                //Permitindo toque na tela novamente
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                //Desativando a barra de loading
                progressBar.visibility = View.INVISIBLE

                //Iniciando nova atividade
                startActivity(intent)

            }.addOnFailureListener {

                Toast.makeText(this, "Ocorreu um erro durante o cadastro.", Toast.LENGTH_LONG)
                    .show()
                Log.e("Test", it.message.toString(), it)

            }
    }

    private fun createPerson(id: String?, name: String, email: String, phone: String) {

        val person =
            Person(id, name, email, defaultProfileImageUrl, phone)

        FirebaseFirestore.getInstance().collection("person")
            .document(id.toString())
            .set(person)
            .addOnSuccessListener {

                val intent = Intent(this, TopActivity::class.java)

                //Permitindo toque na tela novamente
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                //Desativando a barra de loading
                progressBar.visibility = View.INVISIBLE

                //Iniciando nova atividade
                startActivity(intent)

            }.addOnFailureListener {

                Toast.makeText(this, "Ocorreu um erro durante o cadastro.", Toast.LENGTH_LONG)
                    .show()
                Log.e("Test", it.message.toString(), it)

            }
    }
}