package com.money.xpensesbookproject.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ReportFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.money.xpensesbookproject.R
import com.money.xpensesbookproject.databinding.ActivityMainBinding
import com.money.xpensesbookproject.ui.category.CategoryFragment
import com.money.xpensesbookproject.ui.dashboard.DashboardFragment
import com.money.xpensesbookproject.ui.dialogs.AddTransactionDialog
import com.money.xpensesbookproject.ui.report.ReportDetailFragment
import com.money.xpensesbookproject.ui.transactions.TransactionsFragment
import com.money.xpensesbookproject.ui.viewmodel.FinanceViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FinanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FinanceViewModel::class.java]

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.bottomNavigationView

        // No ActionBar, just bottom nav
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    Log.d("main", "onCreate: dash")
                    FragmentHandler(DashboardFragment())
                }

                R.id.nav_category ->
                    FragmentHandler(CategoryFragment())

                R.id.nav_report ->
                    FragmentHandler(ReportDetailFragment())

                R.id.nav_transaction ->
                    FragmentHandler(TransactionsFragment())

                else ->{
                    FragmentHandler(DashboardFragment())
                }

            }
            true
        }


        setupFab()
    }


    private fun FragmentHandler(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, fragment).commit()
        true
    }

    private fun setupFab() {
        binding.fabAddTransaction.setOnClickListener {
            showTransactionDialog()
        }
    }

    private fun showTransactionDialog() {
        AddTransactionDialog().show(supportFragmentManager, "AddTransaction")
    }
}
