package com.dev.soarescrf.littlesounds.features.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dev.soarescrf.littlesounds.R
import com.dev.soarescrf.littlesounds.core.utils.UiUtils
import com.dev.soarescrf.littlesounds.databinding.ActivitySplashBinding
import com.dev.soarescrf.littlesounds.features.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Tela de splash exibida ao iniciar o aplicativo.
 * Mostra uma mensagem aleatória de carregamento, toca uma música e redireciona para a MainActivity após um atraso.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Inicializa a UI, configura o status bar, exibe uma mensagem de carregamento e inicia o som.
     * Após 3 segundos, navega para a [MainActivity].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplica preenchimento de sistema (barras de status/navigation) na raiz da view
        ViewCompat.setOnApplyWindowInsetsListener(binding.splashRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        UiUtils.setLightStatusBar(this, true)
        showLoadingMessage()
        playLoadingSound()

        // Aguarda 3 segundos antes de navegar para a tela principal
        lifecycleScope.launch {
            delay(3000)
            navigateToMain()
        }
    }

    /**
     * Libera os recursos do [MediaPlayer] ao pausar a activity.
     */
    override fun onPause() {
        super.onPause()
        stopAndReleaseMediaPlayer()
    }

    /**
     * Libera recursos e restaura o estado do status bar ao destruir a activity.
     */
    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseMediaPlayer()
        UiUtils.setLightStatusBar(this, false)
    }

    /**
     * Exibe uma mensagem de carregamento aleatória definida no arquivo `strings.xml`.
     */
    private fun showLoadingMessage() {
        val messages = resources.getStringArray(R.array.loading_all_messages)
        binding.loadingMessage.text = messages.random()
    }

    /**
     * Inicia a reprodução da música de carregamento em loop.
     */
    private fun playLoadingSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.loading_music)?.apply {
                isLooping = true
                start()
            }
        }
    }

    /**
     * Para e libera os recursos do [MediaPlayer], evitando vazamentos de memória.
     */
    private fun stopAndReleaseMediaPlayer() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }

    /**
     * Navega para a [MainActivity] e finaliza a SplashActivity.
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
