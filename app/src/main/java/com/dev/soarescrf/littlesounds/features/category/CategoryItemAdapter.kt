package com.dev.soarescrf.littlesounds.features.category

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.soarescrf.littlesounds.databinding.AdapterItemContainerBinding
import java.io.IOException

/**
 * Adaptador responsável por exibir os itens de uma categoria em uma lista.
 * Cada item possui imagem, nome e um som associado que pode ser reproduzido ao clicar.
 *
 * @param context Contexto da aplicação para inflar views e tocar som.
 */
class CategoryItemAdapter(
    private val context: Context
) : ListAdapter<CategoryItem, CategoryItemAdapter.ViewHolder>(ItemDiffCallback()) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Cria um novo [ViewHolder] para a lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterItemContainerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Associa os dados de um [CategoryItem] a uma célula da lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder responsável por renderizar e interagir com um item da lista.
     *
     * @property binding Bind do layout do item.
     */
    inner class ViewHolder(
        private val binding: AdapterItemContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Associa os dados do item às views, e define o comportamento de clique.
         *
         * @param item Item da categoria contendo nome, imagem e som.
         */
        fun bind(item: CategoryItem) = with(binding) {
            txtItemLabel.text = item.name

            Glide.with(root)
                .load(item.imageUrl)
                .into(imgItem)

            val clickListener = {
                animateCard()
                playSound(item)
            }

            root.setOnClickListener { clickListener() }
            imgItem.setOnClickListener { clickListener() }
        }

        /**
         * Aplica uma animação de clique no card, com efeito de escala.
         */
        private fun animateCard() {
            binding.root.animate().apply {
                scaleX(0.97f)
                scaleY(0.97f)
                setDuration(100)
                withEndAction {
                    binding.root.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                start()
            }
        }

        /**
         * Reproduz o som associado ao item clicado.
         * Em caso de erro, exibe um Toast.
         *
         * @param item Item com a URL do som.
         */
        private fun playSound(item: CategoryItem) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(item.soundUrl)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Erro ao tocar o som", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback utilizado para otimizar atualizações de itens na lista
     * com base nas diferenças entre os dados antigos e novos.
     */
    class ItemDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {

        /**
         * Verifica se os dois itens representam o mesmo objeto.
         */
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Verifica se o conteúdo dos dois itens é igual.
         */
        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
