package com.petersonlabs.personalfinancetool.api

import com.petersonlabs.personalfinancetool.Constants
import com.petersonlabs.personalfinancetool.data.CategoryRepository
import com.petersonlabs.personalfinancetool.data.TransactionRepository
import com.petersonlabs.personalfinancetool.data.VendorRepository
import com.petersonlabs.personalfinancetool.model.*
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@RestController
@RequestMapping("transaction")
class TransactionResource(
    val repo: TransactionRepository,
    val categoryRepo: CategoryRepository,
    @PersistenceContext
    val entityManager: EntityManager
) {


    @PostMapping("/uploadCSV", consumes = [MULTIPART_FORM_DATA_VALUE])
    @Tag(name = "Transaction")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): String? {
        val bufferedReader = BufferedReader(InputStreamReader(file.inputStream));

        val csvParser = CSVParser(
            bufferedReader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
        );

        val transactions = csvParser.map {
            Transaction(
                date = LocalDate.parse(it.get("Date"), DateTimeFormatter.ISO_LOCAL_DATE),
                type = TransactionType.valueOf(it.get("Transaction")),
                description = it.get("Name"),
                memo = it.get("Memo"),
                amount = it.get("Amount").toDouble()
            )
        }.toList()

        repo.saveAll(transactions)
        return "OK"
    }

    @GetMapping("/matchedByMonth", produces = [APPLICATION_JSON_VALUE])
    @Tag(name = "Transaction")
    fun getMatchedTransactionsByMonth() :List<Any?> {
        val result : MutableList<CategoryTotal> = mutableListOf()
        val zeroTotalCatMap = categoryRepo.findAll()
            .filterNotNull()
            .mapNotNull { it.name }
            .toSet()
            .associateWith { Double.fromBits(0) }

        getMatchedTransactions().stream()
            .map { MatchedTransaction(
                date = LocalDate.of(it.date.year, it.date.month, 1),
                description = it.description,
                amount = it.amount,
                vendor = it.vendor,
                category = it.category
            ) }
            .collect(Collectors.groupingBy { it.date })
            .mapValues { zeroTotalCatMap + it.value.stream()
                .collect(Collectors.groupingBy(
                    { byDate -> byDate.category },
                    Collectors.summingDouble { byCat -> byCat.amount })) }

            .forEach { byDate ->
                byDate.value.mapTo(result) { byCat ->
                    CategoryTotal(date = byDate.key, category = byCat.key, total = byCat.value)
                }
            }
        return result.sortedBy { it.date }
    }


    @GetMapping("/matched", produces = [APPLICATION_JSON_VALUE])
    @Tag(name = "Transaction")
    fun getMatchedTransactions(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): List<MatchedTransaction> {
        return entityManager
            .createNativeQuery(Constants.MATCHED_TRANSACTIONS_QUERY)
            .resultList.map {
                val cols = (it as Array<*>)
                val amountNegator = when(TransactionType.fromOrdinal(cols[3] as Int)) {
                    TransactionType.DEBIT -> -1
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
                (startDate == null || !it.date.isBefore(startDate)) && (endDate == null || !it.date.isAfter(it.date))
            }
    }

    @PutMapping(produces = [TEXT_PLAIN_VALUE], consumes = [APPLICATION_JSON_VALUE])
    @Tag(name = "Transaction")
    fun putTransactions(@RequestBody transactions: List<Transaction>): String {
        repo.saveAll(transactions)
        return "OK"
    }

    @Tag(name = "Transaction")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getTransactions(): MutableIterable<Transaction?> =
        repo.findAll()
}

@RestController
@RequestMapping("vendor")
class VendorResource {
    @Autowired
    lateinit var repo: VendorRepository

    @PutMapping(produces = [TEXT_PLAIN_VALUE], consumes = [APPLICATION_JSON_VALUE])
    @Tag(name = "Vendor")
    fun putVendors(@RequestBody vendors: List<Vendor>): String {
        repo.saveAll(vendors)
        return "OK"
    }

    @Tag(name = "Vendor")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getVendors(): MutableIterable<Vendor?> =
        repo.findAll()
}

@RestController
@RequestMapping("category")
class CategoryResource {
    @Autowired
    lateinit var repo: CategoryRepository

    @PutMapping(produces = [TEXT_PLAIN_VALUE], consumes = [APPLICATION_JSON_VALUE])
    @Tag(name = "Category")
    fun putCategory(@RequestBody categories: List<Category>): String {
        repo.saveAll(categories)
        return "OK"
    }

    @Tag(name = "Category")
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getCategories(): MutableIterable<Category?> =
        repo.findAll()
}