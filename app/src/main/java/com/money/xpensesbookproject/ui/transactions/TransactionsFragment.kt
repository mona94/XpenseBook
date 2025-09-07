package com.money.xpensesbookproject.ui.transactions

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionFilter
import com.money.xpensesbookproject.databinding.FragmentTransactionsBinding
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels()

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        observeTransactions()
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collect { transitions ->
                transactionAdapter.submitList(transitions)
            }
        }
    }


    private fun setupTabLayout() {
        binding.transactionsTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> viewModel.setTransactionFilter(TransactionFilter.ALL)
                        1 -> viewModel.setTransactionFilter(TransactionFilter.INCOME)
                        2 -> viewModel.setTransactionFilter(TransactionFilter.EXPENSE)
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                }
            }
        )
    }

    fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
                showTransactionDetails(transaction)
            },
            onDeleteClick = { transaction ->
                deleteTransaction(transaction)
            }
        )
        binding.lytRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
//            addItemDecoration(
//                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//            )
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete transaction amount  Rs.${transaction.amount}?")
            .setPositiveButton("Delete") { _, _ ->
                // User confirmed → delete
                viewModel.deleteTransaction(transaction)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showTransactionDetails(transaction: Transaction) {
//Will implement this later on
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}