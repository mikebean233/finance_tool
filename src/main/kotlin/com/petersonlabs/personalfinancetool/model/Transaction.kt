package com.petersonlabs.personalfinancetool.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "transaction")
class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null,

    @Column(nullable = false)
    val date: LocalDate? = null,

    @Column(nullable = false)
    val type: TransactionType? = null,

    @Column(nullable = false)
    val description: String? = null,

    @Column(nullable = true)
    val memo: String? = null,

    @Column(nullable = false)
    val amount: Double? = null
)
