package com.petersonlabs.personalfinancetool.api

import com.petersonlabs.personalfinancetool.data.TransactionRepository
import com.petersonlabs.personalfinancetool.model.Transaction
import com.petersonlabs.personalfinancetool.model.TransactionType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class TransactionController(
    private val transactionRepo: TransactionRepository
) {
    fun storeTransactionsFromCSVInputStream(inputStream: InputStream) {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream));

        val existingTransactions = transactionRepo.findAll().toSet()
        val csvParser = CSVParser(
            bufferedReader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
        );

        val transactions = csvParser.map {
            Transaction(
                date = LocalDate.parse(it.get("Date"), DateTimeFormatter.ISO_LOCAL_DATE),
                type = TransactionType.fromName(it.get("Transaction")),
                description = it.get("Name"),
                memo = it.get("Memo"),
                amount = it.get("Amount").toDouble()
            )
        }.filter {!existingTransactions.contains(it)}.toList()

        transactionRepo.saveAll(transactions)
    }
}