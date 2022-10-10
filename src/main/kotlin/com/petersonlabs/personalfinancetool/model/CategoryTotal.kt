package com.petersonlabs.personalfinancetool.model

import java.time.LocalDate

class CategoryTotal(
    val date: LocalDate,
    val category: String,
    val total: Double
) {
}