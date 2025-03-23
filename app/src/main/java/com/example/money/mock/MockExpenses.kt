package  com.example.money.mock

import androidx.compose.ui.graphics.Color
import com.example.money.data.models.Category
import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

val mockCategories = listOf(
    Category("Bills", getRandomColor()),
    Category("Subscriptions", getRandomColor()),
    Category("take out", getRandomColor()),
    Category("Hobbies", getRandomColor())
)

// Function to generate a random color
fun getRandomColor(): Color {
    return Color(
        red = Random.nextFloat(),
        green = Random.nextFloat(),
        blue = Random.nextFloat(),
        alpha = 1f
    )
}

// Function to generate a random Recurrence
fun getRandomRecurrence(): Recurrence {
    return listOf(
        Recurrence.None,
        Recurrence.Daily,
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    ).random()
}

// Function to generate a random note
fun getRandomNote(): String {
    val notes = listOf(
        "Groceries", "Electricity Bill", "Netflix Subscription", "Dinner at a restaurant",
        "Gym Membership", "Coffee", "Car Insurance", "Online Course", "Movie Night"
    )
    return notes.random()
}

// Generating mock expenses
//val mockExpenses: List<Expense> = List(30) {
//    Expense.create(
//        amount = Random.nextDouble(1.0, 1000.0),
//        date = LocalDateTime.now().minus(
//            Random.nextLong(300, 345600), // Subtracting random seconds
//            ChronoUnit.SECONDS
//        ),
//        recurrence = getRandomRecurrence(),
//        note = getRandomNote(),
//        category = mockCategories.random()
//    )
//}


// Function to parse SMS
fun parseSbiUpiSms(sms: String): Map<String, String> {
    val regex = """A/C (\w+) debited by ([\d.]+) on date (\d{1,2}[A-Za-z]{3}\d{2}) trf to (\w+) Refno (\d+).""".toRegex()
    val matchResult = regex.find(sms)

    return if (matchResult != null) {
        val (account, amount, date, recipient, refNo) = matchResult.destructured
        mapOf(
            "account" to account,
            "amount" to amount,
            "date" to date,
            "recipient" to recipient,
            "referenceNo" to refNo
        )
    } else {
        emptyMap()
    }
}

// Sample SMS for mock transactions
val sampleSmsList = listOf(
    "Dear UPI user A/C X6262 debited by 200.0 on date 10Mar25 trf to Chalo Refno 967330515609. If not u? call 1800111109. -SBI",
    "Dear UPI user A/C X1234 debited by 500.5 on date 12Mar25 trf to Flipkart Refno 567890123456. If not u? call 1800111109. -SBI",
    "Dear UPI user A/C X9876 debited by 799.99 on date 08Mar25 trf to Amazon Refno 345678901234. If not u? call 1800111109. -SBI",
    "Dear Customer, DBT/Govt. payment of Rs. 1,500.00 credited to your Acc No. XXXXX159545 on 12/03/25-SBI   ",
    "Dear Customer, INR 8,904.00 credited to your A/c No XX9545 on 06/02/2025 through NEFT with UTR CMS0372583779740 by DELTECS INFOTECH PVT LTD, INFO: BATCHID:0039 DELTECS JANUARY 2025 STIPEND//CMS03-SBI these are the sms "
)

// Mock Expense Generator
val mockExpenses: List<Expense> = sampleSmsList.mapNotNull { sms ->
    val parsedData = parseSbiUpiSms(sms)
    if (parsedData.isNotEmpty()) {
        Expense.create(
            amount = parsedData["amount"]?.toDoubleOrNull() ?: Random.nextDouble(1.0, 1000.0),
            date = LocalDateTime.now().minus(
                Random.nextLong(300, 345600), // Subtracting random seconds
                ChronoUnit.SECONDS
            ),
            recurrence = getRandomRecurrence(),
            note = parsedData["recipient"] ?: "Unknown",
            category = mockCategories.random()
        )
    } else {
        null // Skip if SMS is not parsable
    }
}


