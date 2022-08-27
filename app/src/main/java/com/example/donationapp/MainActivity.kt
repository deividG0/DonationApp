package com.example.donationapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//ESTA É A ATIVIDADE DE LOGIN

class MainActivity : AppCompatActivity() {

    private lateinit var buttonEnter : Button
    private lateinit var imageViewLogo: ImageView
    private lateinit var editTextEmailLogin : TextInputLayout
    private lateinit var editTextPasswordLogin : TextInputLayout
    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingDialog = LoadingDialog(this)

        //Inicializando labels de EditText
        editTextEmailLogin = findViewById(R.id.editTextEmailLogin)
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin)

        //Carregando a imagem de logo a partir do firebase storage

        imageViewLogo = findViewById(R.id.imageViewLogo)
        val uri : String = "https://firebasestorage.googleapis.com/v0/b/donationapp-47a1d.appspot.com/o/donation-app-logo.jpg?alt=media&token=271f6338-94a6-4b78-97bc-6c7aafafe48d"
        Picasso.get()
            .load(uri)
            .into(imageViewLogo)

        //Detecção do clique no botão de "Entrar"

        buttonEnter = findViewById(R.id.buttonEnter)
        buttonEnter.setOnClickListener {

            signIn()

        }

        //Detecção do clique no botão de "Esqueci a senha ?"

        val textViewForgetPassword = findViewById<TextView>(R.id.textViewForgetPassword)
        textViewForgetPassword.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Recuperação de senha")
            val view = layoutInflater.inflate(R.layout.dialog_forget_password,null)
            val email = view.findViewById<EditText>(R.id.editTextEmailForgetPassword)
            builder.setView(view)
            builder.setPositiveButton("Enviar", DialogInterface.OnClickListener { _, _ ->
                Log.i("Test",email.text.toString())
                forgotPassword(email)
            })
            builder.setNegativeButton("Fechar", DialogInterface.OnClickListener { _, _ ->
            })
            builder.show()

        }

        //Detecção de clique no texto de "Crie uma conta"

        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)
        textViewLogin.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    private fun forgotPassword(email: EditText) {
        if(email.text.toString().isEmpty()){
            Toast.makeText(this,"O email não foi informado.", Toast.LENGTH_LONG).show()
            return
        }
        if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches())){
            Toast.makeText(this,"O email está mal formatado.", Toast.LENGTH_LONG).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Email de recuperação de senha enviado.", Toast.LENGTH_LONG).show()
                    Log.i("Test", "Email recuperação enviado")
                }
            }

    }

    private fun signIn() {

        //Esta função realiza o login com o sistema de autenticação do firebase

        val email: String = editTextEmailLogin.editText?.text.toString()
        val password: String = editTextPasswordLogin.editText?.text.toString()

        if (email.isEmpty() || password.isEmpty()) {

            if (email.isEmpty()) {
                editTextEmailLogin.error = "Este campo está em branco."
            }else{
                editTextEmailLogin.error = null
            }
            if (password.isEmpty()) {
                editTextPasswordLogin.error = "Este campo está em branco."
            }else{
                editTextPasswordLogin.error = null
            }
            return
        }

        loadingDialog.startLoadingDialog()

        //Impossibilitando toque na tela pelo usuário
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){

                    val userId = it.result.user!!.uid
                    Log.i("Test",it.result.user!!.uid)

                    val intent = Intent(this, LoadingActivity::class.java)

                    //Permitindo toque na tela novamente
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    loadingDialog.dismissDialog()

                    //Estabelecendo nova atividade como topo da pilha e impossibilitando retorno
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(intent)
                }
            }.addOnFailureListener {

                //Permitindo toque na tela novamente
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                loadingDialog.dismissDialog()

                //Pop-up de alerta
                MaterialAlertDialogBuilder(this,
                    R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons)
                    .setMessage(resources.getString(R.string.errorLoginMessage))
                    .setNeutralButton(resources.getString(R.string.tryAgain)) { dialog, which ->
                    }
                    .show()

                Log.e("Test", it.message.toString(), it)

            }
    }
}