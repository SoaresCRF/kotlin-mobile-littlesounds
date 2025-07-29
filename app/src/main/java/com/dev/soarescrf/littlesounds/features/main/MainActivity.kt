package com.dev.soarescrf.littlesounds.features.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.soarescrf.littlesounds.R
import com.dev.soarescrf.littlesounds.core.utils.TextUtils
import com.dev.soarescrf.littlesounds.core.utils.logError
import com.dev.soarescrf.littlesounds.core.utils.logWarning
import com.dev.soarescrf.littlesounds.core.utils.setGone
import com.dev.soarescrf.littlesounds.core.utils.setVisible
import com.dev.soarescrf.littlesounds.core.utils.showToast
import com.dev.soarescrf.littlesounds.databinding.ActivityMainBinding
import com.dev.soarescrf.littlesounds.databinding.CustomDialogSyncConfirmationBinding
import com.dev.soarescrf.littlesounds.databinding.ViewSyncBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Activity principal do app responsável por exibir as categorias de sons.
 * Também permite a sincronização com a API e lida com diferentes estados da UI.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var syncBinding: ViewSyncBinding
    private val viewModel: MainViewModel by viewModels()

    /**
     * Configura a interface, observa os estados do ViewModel e inicializa a sincronização local.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        syncBinding = ViewSyncBinding.bind(binding.syncView)

        setupEdgeToEdge()
        observeViewModel()

        viewModel.loadFromDatabase(applicationContext)

        binding.fabSync.setOnClickListener { showSyncConfirmationDialog() }
    }

    /**
     * Cancela qualquer job de sincronização em andamento quando a Activity for destruída.
     * Isso evita vazamentos de memória e chamadas de rede desnecessárias.
     */
    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelFetchJob()
    }

    /**
     * Ajusta o padding superior da AppBar para evitar sobreposição com a status bar.
     */
    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }
    }

    /**
     * Observa as mudanças de estado no ViewModel e atualiza a UI conforme necessário.
     */
    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is MainViewState.Idle -> Unit
                is MainViewState.Sync -> handleSyncState()
                is MainViewState.Success -> handleSuccessState(state)
                is MainViewState.Error -> handleErrorState(state)
                is MainViewState.SyncCompleted -> {
                    showToast("Sincronização concluída com sucesso!")
                }
            }
        }
    }

    /**
     * Exibe a tela de sincronização (carregamento).
     */
    private fun handleSyncState() {
        showSyncView(true)
    }

    /**
     * Trata o estado de sucesso ao carregar os dados da API ou banco local.
     * Atualiza a UI com o conteúdo das categorias.
     */
    private fun handleSuccessState(state: MainViewState.Success) = with(binding) {
        val isEmpty = state.categories.all { it.value.isEmpty() }

        showSyncView(false)
        viewModel.resetRetryCount()

        if (isEmpty) {
            viewPager.setGone()
            viewPagerTab.setGone()
            viewEmptyState.root.setVisible()
        } else {
            viewPager.setVisible()
            viewPagerTab.setVisible()
            viewEmptyState.root.setGone()

            val adapter = FeaturePagerAdapter(this@MainActivity, state.categories)
            viewPager.adapter = adapter

            TabLayoutMediator(viewPagerTab, viewPager) { tab, position ->
                tab.text = adapter.getPageTitle(position)
            }.attach()
        }
    }

    /**
     * Trata os erros retornados pelo ViewModel e redireciona para o tipo apropriado.
     */
    private fun handleErrorState(state: MainViewState.Error) {
        val isTimeoutError =
            state.message.contains("Tempo de resposta excedido.", ignoreCase = true)
        val isNoConnectionError =
            state.message.contains("Sem conexão com a internet.", ignoreCase = true)

        when {
            isTimeoutError -> handleTimeoutError()
            isNoConnectionError -> handleNetworkError()
            else -> handleGenericError(state.message)
        }
    }

    /**
     * Lida com erros de timeout e tenta novas sincronizações com limite de tentativas.
     */
    private fun handleTimeoutError() {
        viewModel.retrySyncWithDelay(
            context = applicationContext,
            onRetry = { attempt ->
                showSyncView(true)
                syncBinding.errorServerSlowMessage.text =
                    getRandomMessage(R.array.error_messages_server_slow)
                logWarning(
                    "API",
                    "Tentativa $attempt/${MainViewModel.MAX_TIMEOUT_RETRIES} após timeout"
                )
            },
            onRetryLimitExceeded = {
                showSyncView(false)
                val randomTimeoutErrorMessage =
                    getRandomMessage(R.array.error_messages_server_timeout)
                showToast(randomTimeoutErrorMessage, Toast.LENGTH_LONG)
                logError("API", "Excedido número máximo de tentativas de timeout.")
            }
        )
    }

    /**
     * Lida com erros de conexão de rede e exibe mensagens apropriadas ao usuário.
     */
    private fun handleNetworkError() {
        showSyncView(false)
        val randomNetworkErrorMessage = getRandomMessage(R.array.error_messages_no_connection_user)
        showToast(randomNetworkErrorMessage, Toast.LENGTH_LONG)
        logError("API", "Erro de conexão: sem acesso à internet.")
    }

    /**
     * Lida com erros genéricos e exibe uma mensagem inesperada ao usuário.
     */
    private fun handleGenericError(message: String) {
        showSyncView(false)
        showToast(message)
        val randomGenericErrorMessage = getRandomMessage(R.array.error_messages_unexpected_error)
        showToast(randomGenericErrorMessage, Toast.LENGTH_LONG)
        logError("API", message)
    }

    /**
     * Exibe ou oculta a view de sincronização e altera visibilidade de outros elementos da UI.
     *
     * @param show Define se a tela de sincronização deve ser exibida.
     */
    private fun showSyncView(show: Boolean) = with(binding) {
        if (show) {
            syncView.show()
            appBarLayout.alpha = 0f
            fabSync.hide()
            syncBinding.errorServerSlowMessage.setVisible()
        } else {
            syncView.hide()
            appBarLayout.animate().alpha(1f).setDuration(1000).start()
            fabSync.show()
            syncBinding.errorServerSlowMessage.run {
                setGone()
                text = null
            }
        }
    }

    /**
     * Retorna uma mensagem aleatória de um array de recursos definido no XML.
     *
     * @param arrayResId ID do array de strings.
     * @return Mensagem selecionada aleatoriamente.
     */
    private fun getRandomMessage(arrayResId: Int): String {
        val messageList = resources.getStringArray(arrayResId)
        return messageList.random()
    }

    /**
     * Exibe um diálogo de confirmação para iniciar a sincronização com a API.
     */
    private fun showSyncConfirmationDialog() {
        val dialogBinding = CustomDialogSyncConfirmationBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogBinding.root)
            .create()

        TextUtils.applyJustifyAlignment(dialogBinding.textDialogMessage)

        dialogBinding.imageDialogConfirmation.setOnClickListener {
            binding.viewEmptyState.root.setGone()
            viewModel.resetRetryCount()
            viewModel.syncFromApi(applicationContext)
            dialog.dismiss()
        }

        dialog.show()
    }
}
