package com.example.donationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Configurando barra de loading
        setProgressBar()

        //Inicializando labels de EditText
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextName = findViewById(R.id.editTextName)
        editTextPassword = findViewById(R.id.editTextPassword)

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

        var registered = false

        Toast.makeText(this,"name: $name, email: $email, password: $password, password length: ${password.length}",Toast.LENGTH_LONG).show()

        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {

            if (email.isEmpty()) {
                editTextEmail.error = "Este campo está em branco."
            }else{
                editTextEmail.error = null
            }
            if (name.isEmpty()) {
                editTextName.error = "Este campo está em branco."
            }else{
                editTextName.error = null
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Este campo está em branco."
            }else{
                editTextPassword.error = null
            }
            return
        }

        if(password.length < 6){
            editTextPassword.error = "A senha deve conter pelo menos 6 caracteres."
            return
        }else{
            editTextPassword.error = null
        }

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
        if (registered){

            return

        }

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

    private fun saveUserInDatabase() {

        val id = FirebaseAuth.getInstance().uid
        val name = editTextName.editText?.text.toString()
        val email = editTextEmail.editText?.text.toString()

        val user = User(id, name, email,null)

        FirebaseFirestore.getInstance().collection("users")
            .document(id.toString())
            .set(user)
            .addOnSuccessListener {

                val intent = Intent(this, TopActivity::class.java)

                //Permitindo toque na tela novamente
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                //Iniciando nova atividade
                startActivity(intent)

                //Desativando a barra de loading
                progressBar.visibility = View.INVISIBLE

            }.addOnFailureListener {

                Toast.makeText(this, "Ocorreu um erro durante o cadastro.", Toast.LENGTH_LONG)
                    .show()
                Log.e("Test", it.message.toString(), it)

            }
    }
}