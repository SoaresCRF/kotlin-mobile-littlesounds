package com.dev.soarescrf.littlesounds.features.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.soarescrf.littlesounds.core.error.AppError
import com.dev.soarescrf.littlesounds.features.category.CategoryRepository
import com.dev.soarescrf.littlesounds.features.main.MainViewModel.Companion.MAX_TIMEOUT_RETRIES
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel da MainActivity responsável por gerenciar o estado da UI e as operações
 * relacionadas à sincronização e carregamento de dados das categorias.
 */
class MainViewModel : ViewModel() {

    /** Estado atual da interface. */
    private val _state = MutableLiveData<MainViewState>(MainViewState.Idle)
    val state: LiveData<MainViewState> get() = _state

    /** Contador de tentativas de retry após timeout. */
    private var timeoutRetryCount = 0

    /** Job da operação de busca, para permitir cancelamento. */
    private var fetchJob: Job? = null

    /**
     * Inicia a sincronização com a API e armazena os dados localmente.
     * Em caso de sucesso, carrega os dados do banco e envia estado de sucesso.
     * Em caso de erro, envia estado de erro com mensagem apropriada.
     *
     * @param context Contexto da aplicação (para evitar leaks).
     */
    fun syncFromApi(context: Context) {
        val appContext = context.applicationContext
        cancelFetchJob() // Cancela operação anterior, se houver

        setState(MainViewState.Sync)

        fetchJob = viewModelScope.launch {
            val result = CategoryRepository.fetchFromApiAndSaveLocally(appContext)

            if (result.isSuccess) {
                resetRetryCount()
                loadFromDatabase(appContext)
                setState(MainViewState.SyncCompleted)
            } else {
                val errorMessage = when (val error = result.exceptionOrNull()) {
                    is AppError -> error.message ?: "Erro de rede"
                    else -> "Erro desconhecido"
                }
                setState(MainViewState.Error(errorMessage))
            }
        }
    }

    /**
     * Tenta sincronizar novamente após um atraso exponencial com jitter.
     * Limita o número de tentativas com base em [MAX_TIMEOUT_RETRIES].
     *
     * @param context Contexto da aplicação.
     * @param onRetry Callback executado antes de cada tentativa.
     * @param onRetryLimitExceeded Callback executado ao atingir o limite de tentativas.
     */
    fun retrySyncWithDelay(
        context: Context,
        onRetry: (attempt: Int) -> Unit,
        onRetryLimitExceeded: () -> Unit
    ) {
        if (timeoutRetryCount < MAX_TIMEOUT_RETRIES) {
            timeoutRetryCount++
            onRetry(timeoutRetryCount)

            val totalDelay = calculateBackoffDelay(timeoutRetryCount)

            viewModelScope.launch {
                delay(totalDelay)
                syncFromApi(context)
            }
        } else {
            onRetryLimitExceeded()
        }
    }

    /**
     * Cancela o job de fetch atual (se houver) e limpa a referência.
     * Usado para evitar que a sincronização continue após a tela ser fechada.
     */
    fun cancelFetchJob() {
        fetchJob?.cancel()
        fetchJob = null
    }

    /**
     * Reinicia o contador de tentativas de retry (timeout).
     */
    fun resetRetryCount() {
        timeoutRetryCount = 0
    }

    /**
     * Carrega as categorias salvas localmente no banco de dados.
     *
     * @param context Contexto da aplicação.
     */
    fun loadFromDatabase(context: Context) {
        val appContext = context.applicationContext

        viewModelScope.launch {
            try {
                val categories = CategoryRepository.loadFromDatabase(appContext)
                setState(MainViewState.Success(categories))
            } catch (_: Exception) {
                setState(MainViewState.Error("Erro ao carregar dados locais."))
            }
        }
    }

    /**
     * Atualiza o estado interno observado pela UI.
     *
     * @param state Novo estado.
     */
    private fun setState(state: MainViewState) {
        _state.value = state
    }

    companion object {
        /** Número máximo de tentativas de sincronização após timeout. */
        const val MAX_TIMEOUT_RETRIES = 10

        /** Atraso base para o cálculo de backoff exponencial. */
        private const val BASE_RETRY_DELAY_MILLIS = 2000L

        /** Atraso máximo permitido mesmo após cálculo exponencial. */
        private const val MAX_BACKOFF_DELAY = 20000L

        /**
         * Calcula o atraso para próxima tentativa usando backoff exponencial com jitter.
         *
         * @param attempt Número da tentativa atual.
         * @return Tempo em milissegundos para aguardar antes da próxima tentativa.
         */
        fun calculateBackoffDelay(attempt: Int): Long {
            val exponentialDelay = BASE_RETRY_DELAY_MILLIS * (1 shl (attempt - 1))
            val jitter = Random.nextLong(0, BASE_RETRY_DELAY_MILLIS)
            return (exponentialDelay + jitter).coerceAtMost(MAX_BACKOFF_DELAY)
        }
    }
}
