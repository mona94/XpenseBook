package com.money.xpensesbookproject.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.money.xpensesbookproject.ui.dashboard.DashboardFragment
import com.money.xpensesbookproject.ui.transactions.TransactionsFragment

class FinancePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardFragment()
            1 -> TransactionsFragment()
//            2 -> CategoriesFragment()
            else -> throw IllegalArgumentException("Invalid position : $position")
        }

    }

    override fun getItemCount(): Int = 2  //change to 3 for category fragement

}