package com.petersonlabs.personalfinancetool.api

import com.petersonlabs.personalfinancetool.Constants
import com.petersonlabs.personalfinancetool.data.CategoryRepository
import com.petersonlabs.personalfinancetool.data.DataInitializer
import com.petersonlabs.personalfinancetool.data.TransactionRepository
import com.petersonlabs.personalfinancetool.data.VendorRepository
import com.petersonlabs.personalfinancetool.model.Category
import com.petersonlabs.personalfinancetool.model.CategoryTotal
import com.petersonlabs.personalfinancetool.model.MatchedTransaction
import com.petersonlabs.personalfinancetool.model.Transaction
import com.petersonlabs.personalfinancetool.model.TransactionType
import com.petersonlabs.personalfinancetool.model.Vendor
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.function.Predicate
import java.util.stream.Collectors
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

val getSchema = { t: Class<*> -> t.declaredFields.map { mapOf("name" to it.name, "type" to it.type) } }

@RestController
@RequestMapping("transaction")
class TransactionResource(
    val repo: TransactionRepository,
    val categoryRepo: CategoryRepository,
    @PersistenceContext
    val entityManager: EntityManager,
    val transactionController: TransactionController,
    val dataInitializer: DataInitializer
) {
    val creditCardPaymentFilter = Predicate<MatchedTransaction> {
        !it.description.contains("payment to credit card", true) && !it.description.contains("payment thank you", true)
    }

    @GetMapping("/initialize")
    @Tag(name = "Transaction")
    fun initialize(@RequestParam("initializeTransactions", defaultValue = "false") initializeTransactions: Boolean): String? {
        dataInitializer.initializeData(initializeTransactions)
        return "OK"
    }

    @PostMapping("/uploadCSV", consumes = [MULTIPART_FORM_DATA_VALUE])
    @Tag(name = "Transaction")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): String? {
        transactionController.storeTransactionsFromCSVInputStream(file.inputStream)
        return "OK"
    }

    @GetMapping("/matchedByMonth", produces = [APPLICATION_JSON_VALUE])
    @Tag(name = "Transaction")
    fun getMatchedTransactionsByMonth(): List<Any?> {
        val result: MutableList<CategoryTotal> = mutableListOf()
        val zeroTotalCatMap = categoryRepo.findAll()
            .filterNotNull()
            .mapNotNull { it.name }
            .toSet()
            .associateWith { Double.fromBits(0) }

        getMatchedTransactions().stream()
            .map {
                MatchedTransaction(
                    date = LocalDate.of(it.date.year, it.date.month, 1),
                    description = it.description,
                    amount = it.amount,
                    vendor = it.vendor,
                    category = it.category
                )
            }
            .collect(Collectors.groupingBy { it.date })
            .mapValues {
                zeroTotalCatMap + it.value.stream()
                    .collect(
                        Collectors.groupingBy(
                            { byDate -> byDate.category },
                            Collectors.summingDouble { byCat -> byCat.amount }
                        )
                    )
            }
            .forEach { byDate ->
                byDate.value.mapTo(result) { byCat ->
                    CategoryTotal(date = byDate.key, category = byCat.key, total = byCat.value)
                }
            }
        return result.sortedBy { it.date }
    }

    @GetMapping("/matched", produces = [APPLICATION_JSON_VALUE])
    @Tag(name = "MatchedTransaction")
    fun getMatchedTransactions(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate? = null,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate? = null,
        @RequestParam onlyUnmatched: Boolean? = false
    ): List<MatchedTransaction> {
        return entityManager
            .createNativeQuery(Constants.MATCHED_TRANSACTIONS_QUERY)
            .resultList.map {
                val cols = (it as Array<*>)
                val amountNegator = when (TransactionType.fromOrdinal(cols[3] as Int)) {
                    TransactionType.DEBIT -> -1
                    TransactionType.CHECK -> -1
                    TransactionType.CREDIT -> 1
                    else -> 1
                }
                MatchedTransaction(
                    date = (cols[0] as java.sql.Date).toLocalDate(),
                    description = cols[1] as String,
                    amount = (cols[2] as Double) * amountNegator,
                    vendor = cols[4] as String,
                    category = cols[5] as String
                )
            }.filter {
                (startDate == null || !it.date.isBefore(startDate)) &&
                    (endDate == null || !it.date.isAfter(it.date)) &&
                    creditCardPaymentFilter.test(it) &&
                    (it.category == "uncategorized" || onlyUnmatched != true)
            }
    }

    @Tag(name = "MatchedTransaction")
    @GetMapping("/matched/schema", produces = [APPLICATION_JSON_VALUE])
    fun getMatchedTransactionSchema() =
        getSchema(MatchedTransaction::class.java)

    @PutMapping(produces = [TEXT_PLAIN_VALUE], consumes = [APPLICATION_JSON_VALUE])
    @Tag(name = "Transaction")
    fun putTransactions(@RequestBody transactions: List<Transaction>): String {
        repo.saveAll(transactions)
        return "OK"
    }

    @Tag(name = "Transaction")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getTransactions() =
        repo.findAll()

    @Tag(name = "Transaction")
    @GetMapping("/schema", produces = [APPLICATION_JSON_VALUE])
    fun getTransactionSchema() =
        getSchema(Transaction::class.java)
}

@RestController
@RequestMapping("vendor")
class VendorResource(
    val dataInitilaizer: DataInitializer
) {
    @Autowired
    lateinit var repo: VendorRepository

    @PutMapping(produces = [TEXT_PLAIN_VALUE], consumes = [APPLICATION_JSON_VALUE])
    @Tag(name = "Vendor")
    fun putVendors(@RequestBody vendors: List<Vendor>): String {
        repo.saveAll(vendors)
        return "OK"
    }

    @PostMapping("/uploadCSV", consumes = [MULTIPART_FORM_DATA_VALUE])
    @Tag(name = "Vendor")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): String? {
        dataInitilaizer.initializeVendors(file.inputStream)
        return "OK"
    }

    @Tag(name = "Vendor")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getVendors(): MutableIterable<Vendor?> =
        repo.findAll()

    @Tag(name = "Vendor")
    @GetMapping("/schema", produces = [APPLICATION_JSON_VALUE])
    fun getVendorSchema() =
        getSchema(Vendor::class.java)
}

@RestController
@RequestMapping("category")
class CategoryResource(
    val dataInitializer: DataInitializer
) {
    @Autowired
    lateinit var repo: CategoryRepository

    @PutMapping(produces = [TEXT_PLAIN_VALUE], consumes = [APPLICATION_JSON_VALUE])
    @Tag(name = "Category")
    fun putCategory(@RequestBody categories: List<Category>): String {
        repo.saveAll(categories)
        return "OK"
    }

    @PostMapping("/uploadCSV", consumes = [MULTIPART_FORM_DATA_VALUE])
    @Tag(name = "Category")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): String? {
        dataInitializer.initializeCategories(file.inputStream)
        return "OK"
    }

    @Tag(name = "Category")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getCategories(): MutableIterable<Category?> =
        repo.findAll()

    @Tag(name = "Category")
    @GetMapping("/schema", produces = [APPLICATION_JSON_VALUE])
    fun getCategorySchema() =
        getSchema(Category::class.java)
}
