package com.dev.soarescrf.littlesounds.features.category

import android.content.Context
import com.dev.soarescrf.littlesounds.core.database.AppDatabase
import com.dev.soarescrf.littlesounds.core.database.entities.CategoryItemLocal
import com.dev.soarescrf.littlesounds.core.error.AppError
import com.dev.soarescrf.littlesounds.core.network.RetrofitClient
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repositório responsável por gerenciar o carregamento de dados da API e do banco de dados
 * relacionados às categorias e seus respectivos itens sonoros.
 */
object CategoryRepository {

    /**
     * Busca os dados de categoria da API remota e os salva localmente no banco de dados.
     *
     * @param context Contexto necessário para acessar a instância do banco de dados.
     * @return [Result] com sucesso ou erro, encapsulando qualquer falha como um [AppError].
     */
    suspend fun fetchFromApiAndSaveLocally(context: Context): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val root = RetrofitClient.api.getDynamicData()
                val dataMap = parseCategoryItems(root)

                val db = AppDatabase.Companion.getInstance(context)
                val dao = db.categoryItemDao()

                dao.deleteAllCategoryItems()

                dataMap.forEach { (category, items) ->
                    val entities = items.map {
                        CategoryItemLocal(
                            uniqueId = "$category-${it.id}", // chave composta única
                            id = it.id,
                            name = it.name,
                            imageUrl = it.imageUrl,
                            soundUrl = it.soundUrl,
                            categoryName = category
                        )
                    }
                    dao.insertCategoryItems(entities)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(mapToAppError(e))
            }
        }
    }

    /**
     * Carrega os dados de categoria e itens sonoros diretamente do banco de dados local.
     *
     * @param context Contexto necessário para acessar a instância do banco de dados.
     * @return Mapa de categorias para suas respectivas listas de [CategoryItem].
     */
    suspend fun loadFromDatabase(context: Context): Map<String, List<CategoryItem>> {
        return withContext(Dispatchers.IO) {
            val dao = AppDatabase.Companion.getInstance(context).categoryItemDao()

            val allItems = dao.getAllCategoryItems()

            allItems
                .groupBy { it.categoryName }
                .mapValues { (_, items) ->
                    items.map {
                        CategoryItem(it.id, it.name, it.imageUrl, it.soundUrl)
                    }
                }
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)
        }
    }

    /**
     * Converte o objeto JSON da API em um mapa de categorias contendo listas de [CategoryItem].
     *
     * @param root Objeto JSON raiz retornado pela API.
     * @return Mapa de categorias com seus itens já convertidos.
     */
    private fun parseCategoryItems(root: JsonObject): Map<String, List<CategoryItem>> {
        return root.entrySet().associateTo(LinkedHashMap()) { (key, value) ->
            key.replace("_", " ") to value.asJsonArray.mapNotNull {
                runCatching {
                    val obj = it.asJsonObject
                    CategoryItem(
                        id = obj["id"].asString,
                        name = obj["name"].asString,
                        imageUrl = obj["image_url"].asString,
                        soundUrl = obj["sound_url"].asString
                    )
                }.getOrNull()
            }
        }
    }

    /**
     * Mapeia exceções genéricas para erros definidos na camada de domínio via [AppError].
     *
     * @param e Exceção capturada durante uma operação de rede ou parsing.
     * @return Uma instância de [AppError] correspondente ao tipo de falha.
     */
    private fun mapToAppError(e: Throwable): AppError {
        return when (e) {
            is SocketTimeoutException -> AppError.Timeout
            is UnknownHostException -> AppError.Network
            is IOException -> AppError.Network
            is JsonSyntaxException -> AppError.Parsing
            is HttpException -> mapHttpException(e)
            else -> AppError.Unknown(e.message)
        }
    }

    /**
     * Converte uma exceção HTTP em um tipo de [AppError] mais específico.
     *
     * @param e Exceção [HttpException] contendo código de status e mensagem.
     * @return Um [AppError] correspondente ao código HTTP da resposta.
     */
    private fun mapHttpException(e: HttpException): AppError {
        return when (e.code()) {
            404 -> AppError.NotFound
            503 -> AppError.ServiceUnavailable
            in 500..599 -> AppError.Server
            else -> AppError.HttpError(e.code(), e.message())
        }
    }
}
