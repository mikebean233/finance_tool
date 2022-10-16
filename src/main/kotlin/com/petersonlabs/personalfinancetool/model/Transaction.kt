package com.petersonlabs.personalfinancetool.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "transaction")
data class Transaction(
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
) {
    // excluding id, this is used in case duplicate transactions
    // are uploaded
    override fun equals(other: Any?): Boolean {
        return other != null
                && other is Transaction
                && other.amount == amount
                && other.date == date
                && other.memo == memo
                && other.description == description
                && other.type == type
    }

    override fun hashCode(): Int {
        var result = date?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (memo?.hashCode() ?: 0)
        result = 31 * result + (amount?.hashCode() ?: 0)
        return result
    }
}
