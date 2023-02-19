package com.petersonlabs.personalfinancetool.data

import com.petersonlabs.personalfinancetool.model.Category
import com.petersonlabs.personalfinancetool.model.Transaction
import com.petersonlabs.personalfinancetool.model.Vendor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : CrudRepository<Transaction?, Int?>

@Repository
interface VendorRepository : CrudRepository<Vendor?, Int?>

@Repository
interface CategoryRepository : CrudRepository<Category?, Int?>
