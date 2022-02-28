package com.example.donationapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.children
import androidx.core.view.isEmpty
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*

class ProfileFragment : Fragment() {

    private var selectedUri: Uri? = null
    private lateinit var imgView: ImageView
    private lateinit var buttonPhoto: Button
    private lateinit var textViewDescription : TextView
    private lateinit var textViewAddress : TextView
    private lateinit var textViewPhone : TextView
    private lateinit var editDescriptionProfile : TextView
    private lateinit var editAddressProfile : TextView
    private lateinit var editPhoneProfile : TextView
    private lateinit var userName : TextView
    private lateinit var buttonConversations : Button
    private lateinit var profilePhotoUrl : String
    private lateinit var buttonSolicitations : Button
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        progressBar = view.findViewById(R.id.progressBarLogin)
        progressBar.visibility = View.INVISIBLE

        imgView = view.findViewById(R.id.imageViewPhoto)
        buttonPhoto = view.findViewById(R.id.buttonPhoto)

        textViewDescription = view.findViewById(R.id.textViewDescription)
        textViewAddress = view.findViewById(R.id.textViewAddress)
        textViewPhone = view.findViewById(R.id.textViewPhone)

        editDescriptionProfile = view.findViewById(R.id.editDescriptionProfile)
        editAddressProfile = view.findViewById(R.id.editAddressProfile)
        editPhoneProfile = view.findViewById(R.id.editPhoneProfile)
        userName = view.findViewById(R.id.userName)

        buttonConversations = view.findViewById(R.id.buttonConversations)
        buttonSolicitations = view.findViewById(R.id.buttonSolicitations)

        buttonPhoto.alpha = 1.0f
        fetchInformationProfile()

        buttonSolicitations.setOnClickListener {

            val intent = Intent(context, SolicitationActivity::class.java)
            startActivity(intent)

        }

        buttonConversations.setOnClickListener {

            val intent = Intent(context,ConversationActivity::class.java)
            startActivity(intent)

        }

        editDescriptionProfile.setOnClickListener {

            showInputDialogAlert("Insira a descrição: ", "Descrição", "description")

        }

        editAddressProfile.setOnClickListener {

            showInputDialogAlert("Insira o endereço: ", "Endereço", "address")
        }

        editPhoneProfile.setOnClickListener {

            showInputDialogAlert("Insira o número de telefone: ", "Telefone", "phone")

        }

        buttonPhoto.setOnClickListener {

            selectPhoto()

        }

        return view
    }

    private fun setInformation(field : String, information : String) {

        val currentUserId = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection(UniversalCommunication.userType)
            .document(currentUserId)
            .update(field, information)

    }

    private fun showInputDialogAlert(title: String, hint: String, field : String){

        val dialog = Dialog(context!!)

        var information : String? = null

        dialog.setContentView(R.layout.profile_dialog_input)

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val inputDialogText = dialog.findViewById<TextInputLayout>(R.id.inputDialogText)

        val cancelButtonDialog = dialog.findViewById<Button>(R.id.cancelButtonDialog)
        val confirmButtonDialog = dialog.findViewById<Button>(R.id.confirmButtonDialog)

        inputDialogText.error = null

        dialogTitle.text = title
        inputDialogText.hint = hint

        confirmButtonDialog.setOnClickListener {

            if(inputDialogText.editText?.text!!.isEmpty()){

                inputDialogText.error = "Este campo não foi preenchido"
                return@setOnClickListener

            }
            information = inputDialogText.editText?.text.toString()

            setInformation(field, information!!)
            fetchInformationProfile()

            Log.i("Test",inputDialogText.editText?.text.toString())

            dialog.dismiss()

        }
        cancelButtonDialog.setOnClickListener { dialog.cancel() }

        dialog.show()

    }

    private fun fetchInformationProfile() {

        val currentUserId = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection(UniversalCommunication.userType)
            .document(currentUserId)
            .get()
            .addOnSuccessListener {

                val url = it.get("photoUrl").toString()

                //Log.i("Test", "Deu fetch profile picture, URL: ${it.get("photoUrl").toString()}")

                if(it.get("photoUrl").toString() != UniversalCommunication.defaultProfileImageUrl){

                    Picasso.get()
                        .load(url)
                        .into(imgView)

                    buttonPhoto.alpha = 0.0f

                }

                userName.text = it.get("name").toString()
                textViewPhone.text = it.get("phone").toString()

                if(it.get("description").toString() == "null"){

                    textViewDescription.text = ""
                    textViewDescription.hint = "Este campo não foi preenchido."

                }else{

                    textViewDescription.text = it.get("description").toString()

                }

                if(it.get("address").toString() == "null"){

                    textViewAddress.text = ""
                    textViewAddress.hint = "Este campo não foi preenchido."

                }else{

                    textViewAddress.text = it.get("description").toString()

                }


            }.addOnFailureListener {

                Log.i("Test","Erro ao carregar foto de perfil")

            }

    }

    private fun selectPhoto() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressBar.visibility = View.VISIBLE

        if (requestCode == 0) {

            selectedUri = data?.data
            var bitmap: Bitmap?

            if (selectedUri == null){
                return
            }

            try {

                bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, selectedUri)
                imgView.setImageDrawable(BitmapDrawable(bitmap))
                buttonPhoto.alpha = 0.0f
                getUrlPhoto()

            } catch (e: Exception) {

                Log.e("Test", "Erro em pegar imagem da galeria", e)

            }
        }
    }

    private fun getUrlPhoto() {

        val filename: String = UUID.randomUUID().toString()

        var storageReference =
            FirebaseStorage.getInstance().getReference("profile-pictures/$filename")

        storageReference.putFile(selectedUri!!).addOnSuccessListener {

            Toast.makeText(context, "Successfully uploaded !", Toast.LENGTH_SHORT).show()

            storageReference.downloadUrl.addOnSuccessListener {

                profilePhotoUrl = it.toString()
                changeProfilePicture(profilePhotoUrl)

            }.addOnFailureListener {

                Log.i("Teste", it.message.toString())

            }

        }.addOnFailureListener {

            Log.i("Teste", it.message.toString())

        }
    }

    private fun changeProfilePicture(url: String) {

        val currentUserId = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection(UniversalCommunication.userType)
            .document(currentUserId)
            .update("photoUrl", url)
            .addOnSuccessListener {

                updateRelatedPhoto(url)

            }
    }

    private fun updateRelatedPhoto(url: String){

        val currentUserId = FirebaseAuth.getInstance().uid!!

        progressBar.visibility = View.INVISIBLE

        if(UniversalCommunication.userType == "establishment"){

            FirebaseFirestore.getInstance().collection("offer")
                .get()
                .addOnSuccessListener {
                    for(doc in it){

                        val card = doc.toObject(HomeCardView::class.java)

                        if (card.establishmentId == currentUserId){

                            FirebaseFirestore.getInstance().collection("offer")
                                .document(card.id!!)
                                .update("photoUrl", url)
                                .addOnCompleteListener {

                                    Log.i("Test", "Card updated !")

                                }
                        }
                    }
                }

        }
    }
}