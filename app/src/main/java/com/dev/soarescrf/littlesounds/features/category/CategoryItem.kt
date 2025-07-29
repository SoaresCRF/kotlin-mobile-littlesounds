package com.dev.soarescrf.littlesounds.features.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Representa um item de categoria contendo informações básicas
 * sobre um som, incluindo seu identificador, nome, imagem e URL do som.
 *
 * Esta classe é parcelável para facilitar a passagem entre componentes Android.
 *
 * @property id Identificador único do item.
 * @property name Nome descritivo do item.
 * @property imageUrl URL da imagem associada ao item.
 * @property soundUrl URL do arquivo de áudio associado ao item.
 */
@Parcelize
data class CategoryItem(
    val id: String,
    val name: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("sound_url")
    val soundUrl: String
) : Parcelable
