package com.heliobuzato.whatsappfirebase.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensagem(mesagem: String){
    Toast.makeText(this, mesagem, Toast.LENGTH_LONG).show()
}