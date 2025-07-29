package com.dev.soarescrf.littlesounds.core.error

/**
 * Representa de forma selada os possíveis erros que podem ocorrer na camada de dados do app.
 */
sealed class AppError(message: String) : Throwable(message) {

    // --- Erros de Rede e Servidor ---

    /** Sem conexão com a internet. */
    object Network : AppError("Sem conexão com a internet.")

    /** Tempo de resposta da requisição excedido. */
    object Timeout : AppError("Tempo de resposta excedido.")

    /** O servidor retornou um erro inesperado (ex: HTTP 500). */
    object Server : AppError("Erro no servidor. Tente novamente mais tarde.")

    /** O servidor está temporariamente indisponível (ex: HTTP 503). */
    object ServiceUnavailable : AppError("Serviço temporariamente indisponível.")


    // --- Erros de Dados (Remoto e Local) ---

    /** Erro ao interpretar/converter dados. */
    object Parsing : AppError("Erro ao processar os dados recebidos.")

    /** Recurso não encontrado no servidor (HTTP 404). */
    object NotFound : AppError("O conteúdo que você procura não foi encontrado.")

    /** Erro genérico para outros erros de HTTP. */
    data class HttpError(val code: Int, val errorMessage: String?) :
        AppError("Erro de comunicação: ${errorMessage ?: "código $code"}")


    // --- Erro Genérico ---

    /** Erro desconhecido ou não mapeado. */
    data class Unknown(val causeMessage: String?) :
        AppError("Erro desconhecido: ${causeMessage ?: "sem detalhes"}")
}