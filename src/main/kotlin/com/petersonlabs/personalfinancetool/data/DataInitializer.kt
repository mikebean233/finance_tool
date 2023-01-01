package com.petersonlabs.personalfinancetool.data

import com.petersonlabs.personalfinancetool.api.TransactionController
import com.petersonlabs.personalfinancetool.config.DataFileConfig
import com.petersonlabs.personalfinancetool.model.Category
import com.petersonlabs.personalfinancetool.model.Vendor
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import javax.annotation.PostConstruct

@Component
class DataInitializer(
    dataFileConfig: DataFileConfig,
    val categoryRepo: CategoryRepository,
    val vendorRepo: VendorRepository,
    val transactionController: TransactionController
) {
    private val csvFileRoot = dataFileConfig.root

    @PostConstruct
    fun postConstruct() {
        initializeData()
    }

    fun initializeCategories(categoryIS: InputStream) {
        vendorRepo.deleteAll()
        categoryRepo.deleteAll()

        categoryRepo.saveAll(
            buildCSVParser(categoryIS).map { Category(name = it.get("name")) }
        )
    }

    fun initializeVendors(vendorIS: InputStream) {
        vendorRepo.deleteAll()
        val categories = categoryRepo
            .findAll()
            .filterNotNull()
            .associateBy { it.name }

        vendorRepo.saveAll(
            buildCSVParser(vendorIS)
                .map { Vendor(
                    name = it.get("name"),
                    matcher = it.get("matcher"),
                    category = categories[it.get("category")]!!
                ) }
        )
    }

    fun initializeData(initializeTransactions: Boolean = true) {
        if(csvFileRoot.isNotEmpty() && csvFileRoot.isNotBlank()) {
            initializeCategories(UrlResource("$csvFileRoot/category.csv").inputStream)
            initializeVendors(UrlResource("$csvFileRoot/vendor.csv").inputStream)
            if(initializeTransactions)
                transactionController.storeTransactionsFromCSVInputStream(UrlResource("$csvFileRoot/transaction.csv").inputStream)
        }
    }

    private fun buildCSVParser(inputStream: InputStream) : CSVParser {
       return CSVParser(
            BufferedReader(InputStreamReader(inputStream)), CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
        )
    }
}