package com.petersonlabs.personalfinancetool.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "vendor")
class Vendor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    val id: Long? = null,

    @Column(nullable = false, unique = false)
    val name: String? = null,

    @Column(nullable = false, unique = false)
    val matcher: String? = null,

    @ManyToOne
    @JoinColumn(nullable = false)
    val category: Category? = null
)
