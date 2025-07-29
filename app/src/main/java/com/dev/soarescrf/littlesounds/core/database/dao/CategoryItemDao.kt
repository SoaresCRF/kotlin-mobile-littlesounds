package com.dev.soarescrf.littlesounds.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.soarescrf.littlesounds.core.database.entities.CategoryItemLocal

/**
 * DAO para operações relacionadas a [CategoryItemLocal] na base de dados.
 */
@Dao
interface CategoryItemDao {

    /**
     * Insere uma lista de itens de categoria na base de dados.
     * Em caso de conflito, substitui os registros existentes.
     *
     * @param items Lista de [CategoryItemLocal] a ser inserida.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryItems(items: List<CategoryItemLocal>)

    /**
     * Busca todos os itens de categoria armazenados na base de dados.
     *
     * @return Lista de todos os [CategoryItemLocal] armazenados.
     */
    @Query("SELECT * FROM category_items")
    suspend fun getAllCategoryItems(): List<CategoryItemLocal>

    /**
     * Remove todos os itens da tabela de categorias.
     */
    @Query("DELETE FROM category_items")
    suspend fun deleteAllCategoryItems()
}
