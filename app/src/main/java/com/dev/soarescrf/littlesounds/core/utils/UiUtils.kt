package com.dev.soarescrf.littlesounds.core.utils

import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat

object UiUtils {

    /**
     * Define se os ícones da status bar serão escuros (true) ou claros (false).
     */
    fun setLightStatusBar(activity: Activity, isLight: Boolean) {
        WindowInsetsControllerCompat(
            activity.window,
            activity.window.decorView
        ).isAppearanceLightStatusBars = isLight
    }
}