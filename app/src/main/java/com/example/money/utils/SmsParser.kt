package com.example.money.utils

import android.util.Log
import com.example.money.data.models.Category
import com.example.money.data.models.KeywordMapping
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

data class ParsedTransaction(
    val amount: Double,
    val date: LocalDate?,
    val merchant: String,
    val referenceNo: String,
    val account: String?,
    val type :String
)

object SmsParser {

    private val debitPattern = Regex(
        """A/C\s(\w+)\sdebited\sby\s([\d,]+(?:\.\d+)?)\son\sdate\s(\d{1,2}[A-Za-z]{3}\d{2})\strf\sto\s([\w\s]+)\sRefno\s(\d+)""",
        RegexOption.IGNORE_CASE
    )

    private val creditGovtPattern = Regex(
        """payment of Rs\. ([\d,]+(?:\.\d+)?) credited to your Acc No\. \w*(\d{4,}) on (\d{2}/\d{2}/\d{2})""",
        RegexOption.IGNORE_CASE
    )

    private val creditNeftPattern = Regex(
        """INR ([\d,]+(?:\.\d+)?) credited to your A/c No \w*(\d{4,}) on (\d{2}/\d{2}/\d{4}).*? by ([\w\s.]+)""",
        RegexOption.IGNORE_CASE
    )

    fun parseSms(message: String): ParsedTransaction? {
        return when {
            debitPattern.containsMatchIn(message) -> {
                val match = debitPattern.find(message) ?: return null
                val (account, amount, dateStr, merchant, referenceNo) = match.destructured
                ParsedTransaction(
                    account = account,
                    amount = amount.replace(",", "").toDouble(),
                    date = parseDateFromDdMMMyy(dateStr),
                    merchant = merchant,
                    referenceNo = referenceNo,
                    type = "debit"
                )
            }

            creditGovtPattern.containsMatchIn(message) -> {
                val match = creditGovtPattern.find(message) ?: return null
                val (amount, account, dateStr) = match.destructured
                ParsedTransaction(
                    account = account,
                    amount = amount.replace(",", "").toDouble(),
                    date = parseDateFromDdMMyy(dateStr),
                    merchant = "Govt DBT",
                    referenceNo = "N/A",
                    type = "credit"
                )
            }

            creditNeftPattern.containsMatchIn(message) -> {
                val match = creditNeftPattern.find(message) ?: return null
                val (amount, account, dateStr, merchant) = match.destructured
                ParsedTransaction(
                    account = account,
                    amount = amount.replace(",", "").toDouble(),
                    date = parseDateFromDdMMyyyy(dateStr),
                    merchant = merchant,
                    referenceNo = "N/A",
                    type = "credit"
                )
            }

            else -> {
                Log.d("SmsParser", "❌ No match found in: $message")
                null
            }
        }
    }

    private fun parseDateFromDdMMMyy(dateStr: String): LocalDate =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH))

    private fun parseDateFromDdMMyy(dateStr: String): LocalDate =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH))

    private fun parseDateFromDdMMyyyy(dateStr: String): LocalDate =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH))
}

fun mapMerchantToCategory(
    merchant: String,
    realm: Realm
): Category {
    // 1. Normalize merchant input
    val normalizedMerchant = merchant.lowercase()

    // 2. Find matching keyword from stored mappings
    val allMappings = realm.query<KeywordMapping>().find()
    val matchedMapping = allMappings.firstOrNull {
        normalizedMerchant.contains(it.keyword.lowercase())
    }

    val categoryName = matchedMapping?.categoryName ?: "Uncategorized"

    // 3. Try to find existing category by name
    val existingCategory = realm.query<Category>("name == $0", categoryName).first().find()
    if (existingCategory != null) return existingCategory

    // 4. Create and persist if not found (including "Uncategorized")
    val newCategory = Category(
        name = categoryName,
        color = generateColorForCategory(categoryName)
    )

    return realm.writeBlocking {
        copyToRealm(newCategory)
    }
}






