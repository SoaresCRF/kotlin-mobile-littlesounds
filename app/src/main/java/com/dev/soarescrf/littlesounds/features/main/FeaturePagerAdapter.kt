package com.dev.soarescrf.littlesounds.features.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dev.soarescrf.littlesounds.features.category.CategoryFragment
import com.dev.soarescrf.littlesounds.features.category.CategoryItem

/**
 * Adapter para o ViewPager2 que exibe uma lista de [CategoryFragment]s,
 * cada um representando uma categoria de sons com seus respectivos itens.
 *
 * @param activity A atividade que hospeda o ViewPager2.
 * @param categories Mapa contendo nomes das categorias como chave e uma lista de [CategoryItem] como valor.
 */
class FeaturePagerAdapter(
    activity: AppCompatActivity,
    private val categories: Map<String, List<CategoryItem>>
) : FragmentStateAdapter(activity) {

    /** Lista ordenada de nomes de categorias, usada para indexar os fragments. */
    private val categoryNames = categories.keys.toList()

    /**
     * Retorna a quantidade de categorias/fragments que o ViewPager exibirá.
     */
    override fun getItemCount(): Int = categoryNames.size

    /**
     * Cria um novo [CategoryFragment] para a posição especificada,
     * baseado no nome da categoria e seus itens.
     *
     * @param position A posição da aba/categoria no ViewPager.
     * @return Um [Fragment] instanciado com os dados da categoria.
     */
    override fun createFragment(position: Int): Fragment {
        val category = categoryNames[position]
        val items = categories[category] ?: emptyList()
        return CategoryFragment.newInstance(category, items)
    }

    /**
     * Retorna o título da aba com a primeira letra em maiúscula,
     * usada em conjunto com o [com.google.android.material.tabs.TabLayoutMediator].
     *
     * @param position A posição da aba.
     * @return O nome formatado da categoria.
     */
    fun getPageTitle(position: Int): String {
        return categoryNames[position].replaceFirstChar { it.uppercaseChar() }
    }
}
