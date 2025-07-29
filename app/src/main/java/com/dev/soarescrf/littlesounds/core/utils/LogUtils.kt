package com.dev.soarescrf.littlesounds.core.utils

import android.util.Log

/**
 * Registra uma mensagem de erro no log do sistema.
 *
 * @param tag A tag usada para identificar a origem do log.
 * @param message A mensagem de erro a ser registrada.
 */
fun logError(tag: String, message: String) {
    Log.e(tag, message)
}

/**
 * Registra uma mensagem de aviso no log do sistema.
 *
 * @param tag A tag usada para identificar a origem do log.
 * @param message A mensagem de aviso a ser registrada.
 */
fun logWarning(tag: String, message: String) {
    Log.w(tag, message)
}
