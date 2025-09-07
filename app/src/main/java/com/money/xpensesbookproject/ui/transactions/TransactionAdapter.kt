package com.money.xpensesbookproject.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.money.xpensesbookproject.R
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val binding: ItemTransactionBinding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }


    /* Adpater view  model holder*/
    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        private val currencyFormatter = NumberFormat.getCurrencyInstance()

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

        }

        fun bind(transaction: Transaction) {
            binding.apply {
                txtDescription.text = transaction.description
                txtCategory.text = transaction.category
                txtDate.text = dateFormatter.format(Date(transaction.date))

                // Format amount with currency symbol and color based on transaction type
                val amount = currencyFormatter.format(transaction.amount)
                txtAmount.text = when (transaction.type) {
                    TransactionType.INCOME -> "+$amount"
                    TransactionType.EXPENSE -> "-$amount"
                }

                // Set text color based on transaction type
                txtAmount.setTextColor(
                    txtAmount.context.getColor(
                        when (transaction.type) {
                            TransactionType.INCOME -> R.color.income_green
                            TransactionType.EXPENSE -> R.color.expenses_red
                        }
                    )
                )
            }
        }
    }


    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

