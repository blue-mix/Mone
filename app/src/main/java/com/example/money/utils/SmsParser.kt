package com.example.money.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.money.models.Category
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import io.realm.kotlin.Realm

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
//
//fun mapMerchantToCategory(merchant: String, allCategories: List<Category>): Category {
//    val mappings = mapOf(
//        // 🛒 Food & Dining
//        "zomato" to "Food",
//        "swiggy" to "Food",
//        "dominos" to "Food",
//        "mcdonalds" to "Food",
//        "pizza hut" to "Food",
//        "ubereats" to "Food",
//        "kfc" to "Food",
//        "burger king" to "Food",
//        "cafecoffee" to "Food",
//        "starbucks" to "Food",
//
//        // 🚗 Transport
//        "uber" to "Transport",
//        "ola" to "Transport",
//        "rapido" to "Transport",
//        "redbus" to "Transport",
//        "blablacar" to "Transport",
//        "chalo" to "Transport",
//        "irctc" to "Transport",
//        "railways" to "Transport",
//
//        // 🛍️ Shopping
//        "amazon" to "Shopping",
//        "flipkart" to "Shopping",
//        "myntra" to "Shopping",
//        "ajio" to "Shopping",
//        "nykaa" to "Shopping",
//        "meesho" to "Shopping",
//        "snapdeal" to "Shopping",
//
//        // 🎬 Entertainment
//        "netflix" to "Entertainment",
//        "hotstar" to "Entertainment",
//        "prime video" to "Entertainment",
//        "spotify" to "Entertainment",
//        "gaana" to "Entertainment",
//        "zee5" to "Entertainment",
//        "bookmyshow" to "Entertainment",
//        "sonyliv" to "Entertainment",
//
//
//        // 🧾 Bills
//        "electricity" to "Bills",
//        "recharge" to "Bills",
//        "broadband" to "Bills",
//        "airtel" to "Bills",
//        "jio" to "Bills",
//        "bsnl" to "Bills",
//        "datacard" to "Bills",
//        "tatasky" to "Bills",
//        "dth" to "Bills",
//        "postpaid" to "Bills",
//
//        // ✈️ Travel
//        "irctc" to "Travel",
//        "makemytrip" to "Travel",
//        "goibibo" to "Travel",
//        "yatra" to "Travel",
//        "airindia" to "Travel",
//        "indigo" to "Travel",
//        "vistara" to "Travel",
//        "cleartrip" to "Travel",
//
//        // 💼 Income
//        "salary" to "Income",
//        "stipend" to "Income",
//        "credit" to "Income",
//        "deltecs" to "Income",
//        "rebate" to "Income",
//        "incentive" to "Income",
//        "bonus" to "Income",
//        "refund" to "Income",
//
//        // 🏧 ATM & Cash
//        "atm" to "Cash Withdrawal",
//        "cash" to "Cash Withdrawal",
//
//        // 🔄 Transfers
//        "upi" to "Transfers",
//        "neft" to "Transfers",
//        "rtgs" to "Transfers",
//        "imps" to "Transfers",
//        "phonepe" to "Transfers",
//        "gpay" to "Transfers",
//        "google pay" to "Transfers",
//        "paytm" to "Transfers",
//        "bhim" to "Transfers",
//
//        // 🏥 Health
//        "pharmacy" to "Health",
//        "medplus" to "Health",
//        "apollo" to "Health",
//        "practo" to "Health",
//        "1mg" to "Health",
//        "pharmeasy" to "Health",
//        "govt" to "Scholarship",
//
//        // 🏠 Rent
//        "rent" to "Rent",
//        "no broker" to "Rent",
//        "housing.com" to "Rent"
//    )
//
////    val mappedName = mappings.entries.firstOrNull { merchant.contains(it.key, ignoreCase = true) }?.value
////    return allCategories.firstOrNull { it.name.equals(mappedName, ignoreCase = true) }
////        ?: allCategories.firstOrNull { it.name.equals("Uncategorized", ignoreCase = true) }
////        ?: Category.create("Uncategorized", Color.Gray)
////    val matchedCategory = mappings.entries.firstOrNull { (keyword, _) ->
////        merchant.contains(keyword, ignoreCase = true)
////    }?.value ?: "Uncategorized"
////
////    return allCategories.firstOrNull { it.name.equals(matchedCategory, ignoreCase = true) }
////        ?: allCategories.firstOrNull { it.name.equals("Uncategorized", ignoreCase = true) }
////        ?: Category.create("Uncategorized", Color.Gray)
//    val matchedCategoryName = mappings.entries.firstOrNull { (keyword, _) ->
//        merchant.contains(keyword, ignoreCase = true)
//    }?.value ?: "Uncategorized"
//
//    return allCategories.firstOrNull {
//        it.name.equals(matchedCategoryName, ignoreCase = true)
//    } ?: allCategories.firstOrNull {
//        it.name.equals("Uncategorized", ignoreCase = true)
//    } ?: Category(name = "Uncategorized", _colorValue = Color.Gray.toString())
//}



fun mapMerchantToCategory(
    merchant: String,
    allCategories: List<Category>, realm: Realm
): Category {
    val mappings = mapOf(
         //🛒 Food & Dining
        "zomato" to "Food",
        "swiggy" to "Food",
        "dominos" to "Food",
        "mcdonalds" to "Food",
        "pizza hut" to "Food",
        "ubereats" to "Food",
        "kfc" to "Food",
        "burger king" to "Food",
        "cafecoffee" to "Food",
        "starbucks" to "Food",

        // 🚗 Transport
        "uber" to "Transport",
        "ola" to "Transport",
        "rapido" to "Transport",
        "redbus" to "Transport",
        "blablacar" to "Transport",
        "chalo" to "Transport",
        "irctc" to "Transport",
        "railways" to "Transport",

        // 🛍️ Shopping
        "amazon" to "Shopping",
        "flipkart" to "Shopping",
        "myntra" to "Shopping",
        "ajio" to "Shopping",
        "nykaa" to "Shopping",
        "meesho" to "Shopping",
        "snapdeal" to "Shopping",

        // 🎬 Entertainment
        "netflix" to "Entertainment",
        "hotstar" to "Entertainment",
        "prime video" to "Entertainment",
        "spotify" to "Entertainment",
        "gaana" to "Entertainment",
        "zee5" to "Entertainment",
        "bookmyshow" to "Entertainment",
        "sonyliv" to "Entertainment",


        // 🧾 Bills
        "electricity" to "Bills",
        "recharge" to "Bills",
        "broadband" to "Bills",
        "airtel" to "Bills",
        "jio" to "Bills",
        "bsnl" to "Bills",
        "datacard" to "Bills",
        "tatasky" to "Bills",
        "dth" to "Bills",
        "postpaid" to "Bills",

        // ✈️ Travel
        "irctc" to "Travel",
        "makemytrip" to "Travel",
        "goibibo" to "Travel",
        "yatra" to "Travel",
        "airindia" to "Travel",
        "indigo" to "Travel",
        "vistara" to "Travel",
        "cleartrip" to "Travel",

        // 💼 Income
        "salary" to "Income",
        "stipend" to "Income",
        "credit" to "Income",
        "deltecs" to "Income",
        "rebate" to "Income",
        "incentive" to "Income",
        "bonus" to "Income",
        "refund" to "Income",

        // 🏧 ATM & Cash
        "atm" to "Cash Withdrawal",
        "cash" to "Cash Withdrawal",

        // 🔄 Transfers
        "upi" to "Transfers",
        "neft" to "Transfers",
        "rtgs" to "Transfers",
        "imps" to "Transfers",
        "phonepe" to "Transfers",
        "gpay" to "Transfers",
        "google pay" to "Transfers",
        "paytm" to "Transfers",
        "bhim" to "Transfers",

        // 🏥 Health
        "pharmacy" to "Health",
        "medplus" to "Health",
        "apollo" to "Health",
        "practo" to "Health",
        "1mg" to "Health",
        "pharmeasy" to "Health",
        "govt" to "Scholarship",

        // 🏠 Rent
        "rent" to "Rent",
        "no broker" to "Rent",
        "housing.com" to "Rent"
    )

    val matchedCategoryName = mappings.entries.firstOrNull { (keyword, _) ->
        merchant.contains(keyword, ignoreCase = true)
    }?.value ?: "Uncategorized"

    val existingCategory = allCategories.firstOrNull {
        it.name.equals(matchedCategoryName, ignoreCase = true)
    }

    if (existingCategory != null) {
        return existingCategory
    }

    // Check if "Uncategorized" exists
    val uncategorized = allCategories.firstOrNull {
        it.name.equals("Uncategorized", ignoreCase = true)
    }
    if (matchedCategoryName == "Uncategorized" || uncategorized != null) {
        return uncategorized ?: Category(name = "Uncategorized", color = Color.Gray)
    }

    // Otherwise: Dynamically create and persist new Category
    val newCategory = Category(name = matchedCategoryName, color = generateColorForCategory(matchedCategoryName))

    realm.writeBlocking {
        copyToRealm(newCategory)
    }

    return newCategory
}



