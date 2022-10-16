package com.petersonlabs.personalfinancetool.data

import com.petersonlabs.personalfinancetool.api.TransactionController
import com.petersonlabs.personalfinancetool.config.DataFileConfig
import com.petersonlabs.personalfinancetool.model.Category
import com.petersonlabs.personalfinancetool.model.Vendor
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
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

    fun initializeData(initializeTransactions: Boolean = true) {
        if(csvFileRoot.isNotEmpty() && csvFileRoot.isNotBlank()) {
            vendorRepo.deleteAll()
            categoryRepo.deleteAll()

            categoryRepo.saveAll(
                buildCSVParser(ClassPathResource("$csvFileRoot/category.csv").file)
                    .map { Category(name = it.get("name")) }
            )

            val categories = categoryRepo
                .findAll()
                .filterNotNull()
                .associateBy { it.name }

            vendorRepo.saveAll(
                buildCSVParser(ClassPathResource("$csvFileRoot/vendor.csv").file)
                    .map { Vendor(
                        name = it.get("name"),
                        matcher = it.get("matcher"),
                        category = categories[it.get("category")]!!
                    ) }
            )

            if(initializeTransactions)
                transactionController.storeTransactionsFromCSVInputStream(ClassPathResource("$csvFileRoot/transaction.csv").file.inputStream())
        }
    }

    private fun buildCSVParser(file: File) : CSVParser {
       return CSVParser(
            BufferedReader(InputStreamReader(file.inputStream())), CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
        )
    }
}