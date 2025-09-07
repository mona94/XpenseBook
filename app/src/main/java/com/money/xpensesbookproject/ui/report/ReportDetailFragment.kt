package com.money.xpensesbookproject.ui.report

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.money.xpensesbookproject.R
import com.kal.rackmonthpicker.RackMonthPicker
import com.kal.rackmonthpicker.listener.DateMonthDialogListener
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener
import com.money.xpensesbookproject.data.model.DashboardState
import com.money.xpensesbookproject.databinding.FragmentReportBinding
import com.money.xpensesbookproject.ui.transactions.TransactionAdapter
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


class ReportDetailFragment : Fragment() {
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    private var _binding: FragmentReportBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding is null!" }

    val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    private lateinit var transactionAdapter: TransactionAdapter


    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialMonthYear()
        observeDashboardState()
        setupMonthButton()
        setupRecyclerView()
        observeTransactions()

    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactionsList.collect { transitions ->
                transactionAdapter.submitList(transitions)
            }
        }
    }

    fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
            },
            onDeleteClick = { transaction ->
            }, false
        )
        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun setupMonthButton() {
        binding.txtMon.setOnClickListener {
            showMonthPicker()
        }
    }

    private fun showMonthPicker() {

        RackMonthPicker(activity)
            .setColorTheme(R.color.white)
            .setLocale(Locale.ENGLISH)
            .setPositiveButton(object : DateMonthDialogListener {
                override fun onDateMonth(
                    month: Int,
                    startDate: Int,
                    endDate: Int,
                    year: Int,
                    monthLabel: String?
                ) {
                    binding.txtMon.text = months[month - 1]
                    binding.txtYear.text = year.toString()
                    //here change
                    viewModel.setMonthYear(month, year)
                }
            })
            .setNegativeButton(object : OnCancelMonthDialogListener {
                override fun onCancel(dialog: AlertDialog?) {

                }
            }).show()
    }

    private fun observeDashboardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collect { state: DashboardState ->
                updateDashboardUI(state)
            }
        }
    }

    private fun updateDashboardUI(state: DashboardState) {
        binding.apply {
            txtBalance.text = currencyFormatter.format(state.balance)
            txtTotalIncome.text = currencyFormatter.format(state.totalIncome)
            txtTotalExpenses.text = currencyFormatter.format(state.totalExpenses)
        }
    }

    private fun setInitialMonthYear() {
        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1 // 1-based
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        binding.txtMon.text = months[currentMonth - 1]
        binding.txtYear.text = currentYear.toString()

        // Make sure ViewModel also knows about this
        viewModel.setMonthYear(currentMonth, currentYear)
    }

}