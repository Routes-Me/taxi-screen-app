package com.routesme.screen.Class

import android.content.Context
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.routesme.screen.R

class Operations {
    lateinit var context: Context

    companion object{
        @get:Synchronized
        var instance = Operations()
    }

    //Enable / Disable Next Button ...
    fun enableNextButton(button: Button, enable: Boolean) {
        if (enable) {
            button.setBackgroundResource(R.drawable.next_button_border_enable)
            button.isEnabled = true
        } else {
            button.setBackgroundResource(R.drawable.next_button_border_disable)
            button.isEnabled = false
        }
    }

    fun displayAlertDialog(context: Context, title: String, message: String){
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setIcon(android.R.drawable.ic_dialog_alert)
            setPositiveButton("OK"){ dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog: AlertDialog = builder.create().apply {
            setCancelable(false)
        }

        alertDialog.show()
    }
}