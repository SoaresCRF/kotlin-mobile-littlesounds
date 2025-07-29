package com.dev.soarescrf.littlesounds.shared

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.dev.soarescrf.littlesounds.R
import com.dev.soarescrf.littlesounds.core.utils.UiUtils
import com.dev.soarescrf.littlesounds.core.utils.setGone
import com.dev.soarescrf.littlesounds.core.utils.setVisible
import com.dev.soarescrf.littlesounds.databinding.ViewSyncBinding

/**
 * Uma View personalizada que exibe uma animação de carregamento com áudio,
 * utilizada para indicar que uma sincronização ou processo está em andamento.
 *
 * @constructor Cria a [SyncView] com os atributos fornecidos.
 *
 * @param context Contexto no qual a View está sendo utilizada.
 * @param attrs Atributos XML.
 * @param defStyleAttr Estilo padrão a ser aplicado.
 */
class SyncView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSyncBinding
    private var mediaPlayer: MediaPlayer? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = ViewSyncBinding.inflate(inflater, this)
        setGone()
    }

    /**
     * Chamado quando a View é removida da hierarquia de janela.
     *
     * Libera recursos como o MediaPlayer e cancela animações para evitar vazamentos de memória.
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Evitar leaks
        animate().cancel()

        // Liberar o MediaPlayer
        stopAndReleaseMediaPlayer()
    }

    /**
     * Exibe a View, altera a barra de status para modo claro e inicia o som de carregamento.
     */
    fun show() {
        setStatusBarLightMode(true)
        playLoadingSound()
        setVisible()
        alpha = 1f
    }

    /**
     * Oculta a View com ou sem animação e para o som de carregamento.
     *
     * @param animated Define se a ocultação será animada.
     */
    fun hide(animated: Boolean = true) {
        setStatusBarLightMode(false)
        stopAndReleaseMediaPlayer()

        if (animated) {
            animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    setGone()
                    alpha = 1f
                }
                .start()
        } else {
            setGone()
        }
    }

    /**
     * Define o modo da barra de status para claro ou escuro.
     *
     * @param isLight Define se a barra de status será clara.
     */
    private fun setStatusBarLightMode(isLight: Boolean) {
        (context as? Activity)?.let {
            UiUtils.setLightStatusBar(it, isLight)
        }
    }

    /**
     * Inicia o MediaPlayer para tocar uma música de carregamento em loop.
     */
    private fun playLoadingSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.loading_music)?.apply {
                isLooping = true
                start()
            }
        }
    }

    /**
     * Para e libera o MediaPlayer, evitando vazamento de recursos.
     */
    private fun stopAndReleaseMediaPlayer() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }
}
