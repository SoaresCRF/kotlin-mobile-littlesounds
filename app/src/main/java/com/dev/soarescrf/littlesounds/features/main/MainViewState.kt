package com.dev.soarescrf.littlesounds.features.main

import com.dev.soarescrf.littlesounds.features.category.CategoryItem

/**
 * Representa os diferentes estados possíveis da UI principal (Main).
 *
 * Utilizada para controlar o fluxo da interface conforme o estado da sincronização,
 * carregamento de dados e tratamento de erros.
 */
sealed class MainViewState {

    /** Estado inicial, indicando que nenhuma ação está em andamento. */
    object Idle : MainViewState()

    /** Estado que indica que a sincronização de dados está em progresso. */
    object Sync : MainViewState()

    /** Estado que indica que a sincronização foi concluída com sucesso. */
    object SyncCompleted : MainViewState()

    /**
     * Estado que representa o sucesso no carregamento dos dados.
     *
     * @property categories Mapa de categorias contendo listas de itens.
     */
    data class Success(val categories: Map<String, List<CategoryItem>>) : MainViewState()

    /**
     * Estado que representa um erro ocorrido durante a sincronização ou carregamento.
     *
     * @property message Mensagem descritiva do erro.
     */
    data class Error(val message: String) : MainViewState()
}
