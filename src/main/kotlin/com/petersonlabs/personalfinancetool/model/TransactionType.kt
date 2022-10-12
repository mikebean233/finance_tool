package com.petersonlabs.personalfinancetool.model

enum class TransactionType {
    DEBIT,
    CREDIT,
    CHECK;
    companion object {
        fun fromOrdinal(id: Int): TransactionType? = enumValues<TransactionType>().firstOrNull {it.ordinal == id }
        fun fromName(name: String): TransactionType? = name.toIntOrNull()?.let { CHECK } ?: enumValues<TransactionType>().firstOrNull { it.name == name }
    }
}
