package com.dev.soarescrf.littlesounds.core.utils

import android.view.View

/**
 * Torna a [View] visível.
 */
fun View.setVisible() {
    visibility = View.VISIBLE
}

/**
 * Oculta a [View] definindo seu estado como GONE.
 */
fun View.setGone() {
    visibility = View.GONE
}
