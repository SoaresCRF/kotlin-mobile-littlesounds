package com.dev.soarescrf.littlesounds.core.utils

import android.content.Context
import android.widget.Toast

/**
 * Exibe um [Toast] com a mensagem fornecida.
 *
 * @param message Texto a ser exibido no Toast.
 * @param duration Duração da exibição do Toast. Valor padrão é [Toast.LENGTH_SHORT].
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
