package com.mackerel_frontend_aos.data.repository

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.mackerel_frontend_aos.R

class ExceptionDialog(context: Context, header: String, text: String) {
    private val dialog = Dialog(context)
    private val text = text
    private val header = header

    fun showDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.exception_dialog)
        dialog.setCancelable(false)
        val dialog_button = dialog.findViewById<Button>(R.id.dialog_button)
        val dialog_header = dialog.findViewById<TextView>(R.id.dialog_header)
        val dialog_text = dialog.findViewById<TextView>(R.id.dialog_text)

        dialog_header.text = header
        dialog_text.text = text
        dialog.show()

        dialog_button.setOnClickListener {
            dialog.dismiss()
        }
    }
}