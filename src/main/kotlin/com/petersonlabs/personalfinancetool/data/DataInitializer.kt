package com.petersonlabs.personalfinancetool.data

import com.petersonlabs.personalfinancetool.api.TransactionController
import com.petersonlabs.personalfinancetool.config.DataFileConfig
import com.petersonlabs.personalfinancetool.model.Category
import com.petersonlabs.personalfinancetool.model.Transaction
import com.petersonlabs.personalfinancetool.model.Vendor
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.QuoteMode
import org.springframework.core.io.UrlResource
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

private class TableType(
    val repo: CrudRepository<*, *>,
    val resource: UrlResource,
    val type: Class<*>,
    val headers: List<String>,
    val recordBuilder: (Any?) -> List<String?>
)

@Component
class DataInitializer(
    dataFileConfig: DataFileConfig,
    private val categoryRepo: CategoryRepository,
    private val vendorRepo: VendorRepository,
    private val transactionRepo: TransactionRepository,
    private val transactionController: TransactionController
) {
    private val csvFileRoot = dataFileConfig.root
    private val enableInit = dataFileConfig.enableInit
    private val categoryResource = UrlResource("$csvFileRoot/category.csv")
    private val vendorResource = UrlResource("$csvFileRoot/vendor.csv")
    private val transactionResource = UrlResource("$csvFileRoot/transaction.csv")
    private val tableTypes = listOf(
        TableType(categoryRepo, categoryResource, Category::class.java, listOf("name")) { listOf((it as Category).name) },
        TableType(vendorRepo, vendorResource, Vendor::class.java, listOf("name", "matcher", "category")) { listOf((it as Vendor).name, it.matcher, it.category?.name) },
        TableType(transactionRepo, transactionResource, Transaction::class.java, listOf("Date", "Transaction", "Name", "Memo", "Amount")) {
            listOf(
                (it as Transaction).date?.format(DateTimeFormatter.ISO_LOCAL_DATE),
                it.type?.name,
                it.description,
                it.memo,
                it.amount?.toString()
            )
        }
    )

    private val buildTimestamp = {
        val now = LocalDateTime.now()
        "${now.month.value}_${now.dayOfMonth}_${now.year}__${now.hour}_${now.minute}_${now.second}"
    }

    @PostConstruct
    fun postConstruct() {
        if (enableInit) {
            initializeData()
        }
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
                .map {
                    Vendor(
                        name = it.get("name"),
                        matcher = it.get("matcher"),
                        category = categories[it.get("category")]!!
                    )
                }
        )
    }

    fun initializeData(initializeTransactions: Boolean = true) {
        if (csvFileRoot.isNotEmpty() && csvFileRoot.isNotBlank()) {
            initializeCategories(categoryResource.inputStream)
            initializeVendors(vendorResource.inputStream)
            if (initializeTransactions) {
                transactionController.storeTransactionsFromCSVInputStream(transactionResource.inputStream)
            }
        }
    }

    fun backupData() {
        if (csvFileRoot.isNotEmpty() && csvFileRoot.isNotBlank()) {
            tableTypes.forEach {
                backupFile(it.resource, it.type.simpleName.lowercase())
                val csvWriter = buildCSVWriter(it.resource.file.outputStream())
                csvWriter.printRecord(it.headers)
                csvWriter.printRecords(it.repo.findAll().map { record -> it.recordBuilder(record) })
                csvWriter.flush()
                csvWriter.close()
            }
        }
    }

    private fun backupFile(origResource: UrlResource, filePart: String) {
        if (!origResource.isFile) {
            return
        }

        val backupFileResource = UrlResource("$csvFileRoot/${filePart}_${buildTimestamp()}.csv")
        backupFileResource.file.createNewFile()
        origResource.file.copyTo(backupFileResource.file, true)
    }

    private fun buildCSVParser(inputStream: InputStream): CSVParser {
        return CSVParser(
            BufferedReader(InputStreamReader(inputStream)),
            CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
        )
    }

    private fun buildCSVWriter(outputStream: OutputStream): CSVPrinter {
        return CSVPrinter(
            BufferedWriter(OutputStreamWriter(outputStream)),
            CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withQuoteMode(QuoteMode.ALL)
                .withTrim()
        )
    }
}
