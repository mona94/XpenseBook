package com.money.xpensesbookproject.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.money.xpensesbookproject.R
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.databinding.FragmentAddCategoryDialogBinding
import com.money.xpensesbookproject.databinding.FragmentAddTransactionDialogBinding
import com.money.xpensesbookproject.ui.viewmodel.FinanceViewModel
import kotlin.random.Random

class AddCategoryDialog : DialogFragment() {

    private var _binding: FragmentAddCategoryDialogBinding? = null
    private val binding get() = _binding!!


    private lateinit var viewModel: FinanceViewModel

    private var currentType = TransactionType.INCOME

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Convert 350dp to pixels
            val width = (350 * resources.displayMetrics.density).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(R.style.DialogAnimation)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[FinanceViewModel::class.java]

        setupInitialState()
        setupTypeSelection()
        setupButtons()
    }

    private fun setupInitialState() {
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
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupButtons() {
        // Handle save button click
        binding.btnSave.setOnClickListener {
            // Validate and save the transaction
            if (validateInput()) {
                saveCategory()
                dismiss()
            }
        }
        // Handle cancel button click
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }


    fun getRandomHexColor(): Int {
        // generate random 0xRRGGBB
        val colorInt = Random.nextInt(0xFFFFFF + 1)
        // convert to hex string with leading zeros
        val hexColor = String.format("#%06X", colorInt)
        return Color.parseColor(hexColor)
    }

    private fun saveCategory() {
        val categoryName = binding.edtCategory.text.toString()

        val category = Category(
            name = categoryName,
            type = currentType,
            color = getRandomHexColor()
        )

        viewModel.addCategory(category)

    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.edtCategory.text.toString().isEmpty()) {
            binding.edtCategory.error = getString(R.string.error_catoegory_required)
            isValid = false
        }

        return isValid
    }

}