package com.petersonlabs.personalfinancetool.model

import java.time.LocalDate

class MatchedTransaction(
    val date: LocalDate,
    val description: String,
    val amount: Double,
    val vendor: String,
    val category: String
)
