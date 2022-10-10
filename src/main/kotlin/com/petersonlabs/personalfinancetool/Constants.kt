package com.petersonlabs.personalfinancetool

class Constants {
    companion object {
        val MATCHED_TRANSACTIONS_QUERY = """
            ;WITH matchedTransactions AS (
                SELECT
                    T.id,
                    T.date,
                    T.description,
                    T.amount,
                    T.type,
                    V.name AS vendor,
                    C.name AS category
            FROM transaction T
                     JOIN vendor V ON LOWER(T.description) LIKE LOWER(V.matcher)
                     JOIN category C ON C.id = V.category_id),
            unMatchedTransactions AS (
                SELECT
                    T1.date,
                    T1.description,
                    T1.amount,
                    T1.type,
                    'unknown vendor' AS vendor,
                    'uncategorized' AS category
                FROM transaction T1
                WHERE NOT EXISTS (SELECT 1 FROM matchedTransactions T2 WHERE T2.id = T1.id )
                )

            SELECT date, description, amount, type, vendor, category FROM matchedTransactions
                UNION
            SELECT date, description, amount, type, vendor, category FROM unMatchedTransactions
        """.trimIndent()
    }
}