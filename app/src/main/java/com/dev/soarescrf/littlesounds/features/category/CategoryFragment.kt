package com.dev.soarescrf.littlesounds.features.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.soarescrf.littlesounds.R
import com.dev.soarescrf.littlesounds.databinding.FragmentCategoryBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragmento responsável por exibir uma lista de itens de uma categoria específica.
 * Os itens são passados via argumentos no momento da criação do fragmento.
 */
class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoryItemAdapter

    companion object {
        private const val ARG_CATEGORY_NAME = "arg_category_name"
        private const val ARG_ITEMS = "arg_items"

        /**
         * Cria uma nova instância de [CategoryFragment] com os argumentos necessários.
         *
         * @param category Nome da categoria.
         * @param items Lista de itens pertencentes à categoria.
         * @return Instância de [CategoryFragment] com os dados configurados.
         */
        fun newInstance(category: String, items: List<CategoryItem>) = CategoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY_NAME, category)
                putParcelableArrayList(ARG_ITEMS, ArrayList(items))
            }
        }
    }

    /**
     * Infla a view do fragmento, inicializa o adapter e define o layout da lista.
     * Também adiciona comportamento ao botão flutuante (FAB) com base no scroll.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        adapter = CategoryItemAdapter(requireContext())
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = adapter

        // Recupera os itens da categoria a partir dos argumentos
        val items = BundleCompat.getParcelableArrayList(
            requireArguments(),
            ARG_ITEMS,
            CategoryItem::class.java
        ).orEmpty()

        adapter.submitList(items)

        // Comportamento do FAB ao rolar a lista: esconde quando desce, mostra quando sobe
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabSync)
                if (dy > 0 && fab.isShown) {
                    fab.hide()
                } else if (dy < 0 && !fab.isShown) {
                    fab.show()
                }
            }
        })

        return binding.root
    }

    /**
     * Libera o binding da view quando o fragmento for destruído.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
