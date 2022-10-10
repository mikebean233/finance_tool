package com.petersonlabs.personalfinancetool.model

enum class TransactionType {
    DEBIT,
    CREDIT;

    companion object {
        fun fromOrdinal(id: Int): TransactionType? = enumValues<TransactionType>().firstOrNull {it.ordinal == id }
    }
}
