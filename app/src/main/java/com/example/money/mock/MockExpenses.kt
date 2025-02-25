package  com.example.money.mock

import androidx.compose.ui.graphics.Color
import com.example.money.models.Category
import com.example.money.models.Expense
import com.example.money.models.Recurrence

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

val mockCategories = listOf(
    Category.create("Bills", getRandomColor()),
    Category.create("Subscriptions", getRandomColor()),
    Category.create("take out", getRandomColor()),
    Category.create("Hobbies", getRandomColor())
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
val mockExpenses: List<Expense> = List(30) {
    Expense.create(
        amount = Random.nextDouble(1.0, 1000.0),
        date = LocalDateTime.now().minus(
            Random.nextLong(300, 345600), // Subtracting random seconds
            ChronoUnit.SECONDS
        ),
        recurrence = getRandomRecurrence(),
        note = getRandomNote(),
        category = mockCategories.random()
    )
}
