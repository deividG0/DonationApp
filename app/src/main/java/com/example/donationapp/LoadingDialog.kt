package com.example.donationapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog (private val activity: Activity) {

    private lateinit var dialog: AlertDialog

    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(activity)

        val inflater : LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))

        dialog = builder.create()
        dialog.show()

    }

    fun dismissDialog(){

        dialog.dismiss()

    }
}