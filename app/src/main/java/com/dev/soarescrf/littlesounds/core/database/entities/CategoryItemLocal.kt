package com.dev.soarescrf.littlesounds.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa um item de categoria armazenado localmente no banco de dados.
 *
 * @property uniqueId Identificador único para o item na base local.
 * @property id Identificador original do item.
 * @property name Nome do item.
 * @property imageUrl URL da imagem associada ao item.
 * @property soundUrl URL do som associado ao item.
 * @property categoryName Nome da categoria à qual o item pertence.
 */
@Entity(tableName = "category_items")
data class CategoryItemLocal(
    @PrimaryKey val uniqueId: String,
    val id: String,
    val name: String,
    val imageUrl: String,
    val soundUrl: String,
    val categoryName: String
)
