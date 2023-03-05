package com.hanna.textrecognition.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hanna.textrecognition.databinding.LayoutLoadingBarBinding

class LoadingBar(context: Context) {
    private var alertDialog: AlertDialog
    private var isShowing = false

    init {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = LayoutLoadingBarBinding.inflate(inflater)
        builder.setView(binding.root)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        if (!isShowing) {
            isShowing = true
            alertDialog.show()
        }
    }

    fun hide() {
        alertDialog.dismiss()
        isShowing = false
    }
}