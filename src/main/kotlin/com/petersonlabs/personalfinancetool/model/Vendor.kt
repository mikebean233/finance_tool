package com.petersonlabs.personalfinancetool.model

import org.hibernate.annotations.JoinFormula
import javax.persistence.*

@Entity
@Table(name = "vendor")
class Vendor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String? = null,

    @Column(nullable = false, unique = false)
    val matcher: String? = null,

    @ManyToOne
    @JoinColumn(nullable = false)
    val category: Category? = null
)