package com.money.xpensesbookproject.ui.category

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.money.xpensesbookproject.R
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.TransactionFilter
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.databinding.FragmentCategoryBinding
import com.money.xpensesbookproject.databinding.FragmentTransactionsBinding
import com.money.xpensesbookproject.ui.dialogs.AddCategoryDialog
import com.money.xpensesbookproject.ui.transactions.TransactionAdapter
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {


    private lateinit var categoryAdapter: CategoryAdapter
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabLayout()
        setupButton()
        observeCategory()
    }


    fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onItemClick = { category ->
                updateCategoryDetails(category)
            }
        )
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun observeCategory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.category.collect { category ->
                categoryAdapter.submitList(category)
            }
        }
    }

    private fun updateCategoryDetails(category: Category) {

    }

    private fun setupTabLayout() {
        binding.typeTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> viewModel.setCategoryFilter(TransactionType.INCOME)
                        1 -> viewModel.setCategoryFilter(TransactionType.EXPENSE)
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                }
            }
        )
    }

    private fun setupButton() {
        binding.btnAddCategory.setOnClickListener {
            AddCategoryDialog().show(parentFragmentManager,"AddCategory")
        }
    }
}