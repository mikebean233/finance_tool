package com.petersonlabs.personalfinancetool.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "category",
    indexes = [Index(name = "IDX_name", columnList = "name")]
)
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String? = null
)
