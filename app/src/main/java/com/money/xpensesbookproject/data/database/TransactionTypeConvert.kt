package com.money.xpensesbookproject.data.database

import androidx.room.TypeConverter
import com.money.xpensesbookproject.data.model.TransactionType

class TransactionTypeConvert {

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? {
        return value?.let {
            TransactionType.valueOf(it)
        }
    }
}