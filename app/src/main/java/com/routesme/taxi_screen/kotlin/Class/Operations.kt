package com.routesme.taxi_screen.kotlin.Class

import android.widget.Button
import com.routesme.taxiscreen.R

class Operations {
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
}