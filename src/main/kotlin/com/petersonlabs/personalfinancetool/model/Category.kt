package com.petersonlabs.personalfinancetool.model

import javax.persistence.*


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