package com.petersonlabs.personalfinancetool.data

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class DataInitializer(
    @PersistenceContext
    val entityManager: EntityManager
) {

}