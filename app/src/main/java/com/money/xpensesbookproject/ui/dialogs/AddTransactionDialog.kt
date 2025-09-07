package com.money.xpensesbookproject.ui.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.money.xpensesbookproject.R
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.databinding.FragmentAddTransactionDialogBinding
import com.money.xpensesbookproject.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

class AddTransactionDialog : DialogFragment() {
    private var _binding: FragmentAddTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FinanceViewModel
    private var currentType = TransactionType.INCOME
    private var categories = listOf<Category>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Convert 350dp to pixels
//            val width = (350 * resources.displayMetrics.density).toInt()
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            setWindowAnimations(R.style.DialogAnimation)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FinanceViewModel::class.java]

        setupAppBar()
        setupInitialState()
        setupTypeSelection()
        setupCategorySpinner()
        setupButtons()
        observeCategories()
    }

    private fun setupAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Add Transaction"
        }

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    private fun setupInitialState() {
        // Log initial state
        Log.d("AddTransaction", "Initial type: $currentType")

        // Set initial tab based on currentType
        val initialTab = when (currentType) {
            TransactionType.INCOME -> 0
            TransactionType.EXPENSE -> 1
        }
        binding.typeTabLayout.getTabAt(initialTab)?.select()
    }

    private fun setupTypeSelection() {

        binding.typeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val newType = when (tab?.position) {
                    0 -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }

                // Log tab selection
                Log.d("AddTransaction", "Tab selected: ${tab?.position}, New type: $newType")

                if (newType != currentType) {
                    currentType = newType
                    observeCategories()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeCategories() {
        // Observe categories based on selected type
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCategoriesByType(currentType).collect { newCategories ->
                categories = newCategories
                updateCategorySpinner()
            }
        }
    }

    private fun updateCategorySpinner() {
        // Update category dropdown with new categories
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories.map { it.name }
        )
        binding.sppinerCategory.setAdapter(adapter)
    }

    private fun setupCategorySpinner() {
        // Initialize category dropdown with empty adapter
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
        binding.sppinerCategory.setAdapter(adapter)
    }

    private fun setupButtons() {
        // Handle save button click
        binding.btnSave.setOnClickListener {
            // Validate and save the transaction
            if (validateInput()) {
                saveTransaction()
                dismiss()
            }
        }
        // Handle cancel button click
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        // Validate amount
        val amount = binding.edtAmount.text.toString()
        if (amount.isEmpty() || amount.toDoubleOrNull() == null) {
            binding.edtAmount.error = getString(R.string.error_invalid_amount)
            isValid = false
        }

        // Validate description
        if (binding.edtDescription.text.toString().isEmpty()) {
            binding.edtDescription.error = getString(R.string.error_empty_description)
            isValid = false
        }

        // Validate category selection
        if (binding.sppinerCategory.text.toString().isEmpty()) {
            binding.sppinerCategory.error = getString(R.string.error_catoegory_required)
            isValid = false
        }

        return isValid
    }


    private fun saveTransaction() {
        val amount = binding.edtAmount.text.toString().toDouble()
        val description = binding.edtDescription.text.toString()
        val category = binding.sppinerCategory.text.toString()

        // Log transaction creation
        Log.d("AddTransaction", "Saving transaction with type: $currentType")

        val transaction = Transaction(
            amount = amount,
            description = description,
            category = category,
            type = currentType,
            date = System.currentTimeMillis()
        )

        viewModel.addTransaction(transaction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}