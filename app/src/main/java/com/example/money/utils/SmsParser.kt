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

//fun mapMerchantToCategory(
//    merchant: String,
//
//    realm: Realm
//): Category {
//    val mappings = mapOf(
//        // 🛒 Food & Dining
//        "zomato" to "Food", "swiggy" to "Food", "dominos" to "Food", "mcdonalds" to "Food",
//        "pizza hut" to "Food", "ubereats" to "Food", "kfc" to "Food", "burger king" to "Food",
//        "cafecoffee" to "Food", "starbucks" to "Food",
//
//        // 🚗 Transport
//        "uber" to "Transport", "ola" to "Transport", "rapido" to "Transport", "redbus" to "Transport",
//        "blablacar" to "Transport", "chalo" to "Transport", "irctc" to "Transport", "railways" to "Transport",
//
//        // 🛍️ Shopping
//        "amazon" to "Shopping", "flipkart" to "Shopping", "myntra" to "Shopping", "ajio" to "Shopping",
//        "nykaa" to "Shopping", "meesho" to "Shopping", "snapdeal" to "Shopping",
//
//        // 🎬 Entertainment
//        "netflix" to "Entertainment", "hotstar" to "Entertainment", "prime video" to "Entertainment",
//        "spotify" to "Entertainment", "gaana" to "Entertainment", "zee5" to "Entertainment",
//        "bookmyshow" to "Entertainment", "sonyliv" to "Entertainment",
//
//        // 🧾 Bills
//        "electricity" to "Bills", "recharge" to "Bills", "broadband" to "Bills", "airtel" to "Bills",
//        "jio" to "Bills", "bsnl" to "Bills", "datacard" to "Bills", "tatasky" to "Bills",
//        "dth" to "Bills", "postpaid" to "Bills",
//
//        // ✈️ Travel
//        "makemytrip" to "Travel", "goibibo" to "Travel", "yatra" to "Travel", "airindia" to "Travel",
//        "indigo" to "Travel", "vistara" to "Travel", "cleartrip" to "Travel",
//
//        // 💼 Income
//        "salary" to "Income", "stipend" to "Income", "credit" to "Income", "deltecs" to "Income",
//        "rebate" to "Income", "incentive" to "Income", "bonus" to "Income", "refund" to "Income",
//
//        // 🏧 ATM & Cash
//        "atm" to "Cash Withdrawal", "cash" to "Cash Withdrawal",
//
//        // 🔄 Transfers
//        "upi" to "Transfers", "neft" to "Transfers", "rtgs" to "Transfers", "imps" to "Transfers",
//        "phonepe" to "Transfers", "gpay" to "Transfers", "google pay" to "Transfers", "paytm" to "Transfers",
//        "bhim" to "Transfers",
//
//        // 🏥 Health
//        "pharmacy" to "Health", "medplus" to "Health", "apollo" to "Health", "practo" to "Health",
//        "1mg" to "Health", "pharmeasy" to "Health",
//
//        // 🧑‍🎓 Scholarship
//        "govt" to "Scholarship",
//
//        // 🏠 Rent
//        "rent" to "Rent", "no broker" to "Rent", "housing.com" to "Rent"
//    )
//
////    val matchedCategoryName = mappings.entries
////        .firstOrNull { (keyword, _) -> merchant.contains(keyword, ignoreCase = true) }
////        ?.value ?: "Uncategorized"
////
////    val existingCategory = allCategories.firstOrNull {
////        it.name.equals(matchedCategoryName, ignoreCase = true)
////    }
////    if (existingCategory != null) return existingCategory
////
////    val uncategorized = allCategories.firstOrNull {
////        it.name.equals("Uncategorized", ignoreCase = true)
////    }
////    if (matchedCategoryName == "Uncategorized" || uncategorized != null) {
////        return uncategorized ?: Category(name = "Uncategorized", color = Color.Gray)
////    }
////    else{
////    val newCategory = Category(name = matchedCategoryName, color = generateColorForCategory(matchedCategoryName))
////    realm.writeBlocking {
////        copyToRealm(newCategory)
////    }
////    return newCategory}
//    val matchedCategoryName = mappings.entries
//        .firstOrNull { (keyword, _) -> merchant.contains(keyword, ignoreCase = true) }
//        ?.value ?: "Uncategorized"
//
//// 1. Check Realm for an existing category with that name
//    val existingCategory = realm.query<Category>("name == $0", matchedCategoryName).first().find()
//    if (existingCategory != null) return existingCategory
//
//// 2. If matched is "Uncategorized", return existing or create
//    if (matchedCategoryName.equals("Uncategorized", ignoreCase = true)) {
//        val uncategorized = realm.query<Category>("name == $0", "Uncategorized").first().find()
//        return uncategorized ?: realm.writeBlocking {
//            copyToRealm(Category(name = "Uncategorized", color = Color.Gray))
//        }
//    }
//
//// 3. No match found: Create and persist a new category in Realm
//    val newCategory = Category(
//        name = matchedCategoryName,
//        color = generateColorForCategory(matchedCategoryName)
//    )
//
//    realm.writeBlocking {
//        copyToRealm(newCategory)
//    }
//
//    return newCategory
//
//}


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






