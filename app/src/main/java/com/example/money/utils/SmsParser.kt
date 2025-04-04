package com.example.money.utils

import android.util.Log
import com.example.money.data.models.Category
import com.example.money.data.models.KeywordMapping
import com.example.money.utils.SmsParser.parseDateFromDdMMMyy
import com.example.money.utils.SmsParser.parseDateFromDdMMyy
import com.example.money.utils.SmsParser.parseDateFromDdMMyyDashed
import com.example.money.utils.SmsParser.parseDateFromDdMMyyyy
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
data class BankPattern(
    val regex: Regex,
    val bankName: String,
    val type: String, // "credit" or "debit"
    val groupExtractor: (MatchResult) -> ParsedTransaction?
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
    fun parseSmsNew(message: String): ParsedTransaction? {
        for (pattern in bankPatterns) {
            val match = pattern.regex.find(message.replace("\n", " ")) ?: continue
            return pattern.groupExtractor(match)
        }
        return null
    }
    fun parseSms(message: String): ParsedTransaction? {
        return when {
            debitPattern.containsMatchIn(message) -> {
                Log.d("SmsParser", "tick found in: $message")
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
                Log.d("SmsParser", "tick found in: $message")
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
                Log.d("SmsParser", "tick found in: $message")
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

     fun parseDateFromDdMMMyy(dateStr: String): LocalDate =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH))

     fun parseDateFromDdMMyy(dateStr: String): LocalDate =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH))

     fun parseDateFromDdMMyyyy(dateStr: String): LocalDate =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH))

    fun parseDateFromDdMMyyDashed(dateStr: String) =
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yy", Locale.ENGLISH))
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



val bankPatterns = listOf(

    // ✅ Original Debit Pattern
    BankPattern(
        regex = Regex("""A/C\s(\w+)\sdebited\sby\s([\d,]+(?:\.\d+)?)\son\sdate\s(\d{1,2}[A-Za-z]{3}\d{2})\strf\sto\s([\w\s]+)\sRefno\s(\d+)""", RegexOption.IGNORE_CASE),
        bankName = "GenericBank",
        type = "debit",
        groupExtractor = { match ->
            val (account, amount, dateStr, merchant, refNo) = match.destructured
            ParsedTransaction(
                account = account,
                amount = amount.replace(",", "").toDouble(),
                date = parseDateFromDdMMMyy(dateStr),
                merchant = merchant,
                referenceNo = refNo,
                type = "debit"
            )
        }
    ),

    // ✅ Government Credit Pattern
    BankPattern(
        regex = Regex("""payment of Rs\. ([\d,]+(?:\.\d+)?) credited to your Acc No\. \w*(\d{4,}) on (\d{2}/\d{2}/\d{2})""", RegexOption.IGNORE_CASE),
        bankName = "Govt",
        type = "credit",
        groupExtractor = { match ->
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
    ),

    // ✅ NEFT Credit Pattern
    BankPattern(
        regex = Regex("""INR ([\d,]+(?:\.\d+)?) credited to your A/c No \w*(\d{4,}) on (\d{2}/\d{2}/\d{4}).*? by ([\w\s.]+)""", RegexOption.IGNORE_CASE),
        bankName = "GenericBank",
        type = "credit",
        groupExtractor = { match ->
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
    ),

    // ✅ Kotak UPI Credit
    BankPattern(
        regex = Regex("""Received\s+Rs\.?([\d,]+(?:\.\d+)?)\s+in\s+your\s+Kotak\s+Bank\s+AC\s+(\w+)\s+from\s+([\w@.]+)\s+on\s+(\d{2}-\d{2}-\d{2})""", RegexOption.IGNORE_CASE),
        bankName = "Kotak",
        type = "credit",
        groupExtractor = { match ->
            val (amount, account, merchant, dateStr) = match.destructured
            ParsedTransaction(
                account = account,
                amount = amount.replace(",", "").toDouble(),
                merchant = merchant,
                date = parseDateFromDdMMyyDashed(dateStr),
                referenceNo = "N/A",
                type = "credit"
            )
        }
    ),

    // ✅ Kotak UPI Debit
    BankPattern(
        regex = Regex("""Sent\s+Rs\.?([\d,]+(?:\.\d+)?)\s+from\s+Kotak\s+Bank\s+AC\s+(\w+)\s+to\s+([\w@.]+)\s+on\s+(\d{2}-\d{2}-\d{2})""", RegexOption.IGNORE_CASE),
        bankName = "Kotak",
        type = "debit",
        groupExtractor = { match ->
            val (amount, account, merchant, dateStr) = match.destructured
            ParsedTransaction(
                account = account,
                amount = amount.replace(",", "").toDouble(),
                merchant = merchant,
                date = parseDateFromDdMMyyDashed(dateStr),
                referenceNo = "N/A",
                type = "debit"
            )
        }
    ),

    // ✅ HDFC UPI Credit
    BankPattern(
        regex = Regex("""Rs\.?([\d,]+(?:\.\d+)?)\s+credited\s+to\s+HDFC\s+Bank\s+A/c\s+(\w+)\s+on\s+(\d{2}-\d{2}-\d{2})\s+from\s+VPA\s+([\w@.]+)""", RegexOption.IGNORE_CASE),
        bankName = "HDFC",
        type = "credit",
        groupExtractor = { match ->
            val (amount, account, dateStr, merchant) = match.destructured
            ParsedTransaction(
                account = account,
                amount = amount.replace(",", "").toDouble(),
                date = parseDateFromDdMMyyDashed(dateStr),
                merchant = merchant,
                referenceNo = "N/A",
                type = "credit"
            )
        }
    ),

    // ✅ HDFC UPI Debit (multi-line supported)
    BankPattern(
        regex = Regex("""Sent\s+Rs\.?([\d,]+(?:\.\d+)?)\s+From\s+HDFC\s+Bank\s+A/C\s+(\w+).*?To\s+(.*?)\s+On\s+(\d{2}/\d{2}/\d{2})""", setOf(RegexOption.IGNORE_CASE , RegexOption.DOT_MATCHES_ALL)),
        bankName = "HDFC",
        type = "debit",
        groupExtractor = { match ->
            val (amount, account, merchant, dateStr) = match.destructured
            ParsedTransaction(
                account = account,
                amount = amount.replace(",", "").toDouble(),
                date = parseDateFromDdMMyy(dateStr),
                merchant = merchant,
                referenceNo = "N/A",
                type = "debit"
            )
        }
    )
)