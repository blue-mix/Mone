package com.example.money.data.models


import com.example.money.utils.calculateDateRange
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class Expense() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var amount: Double = 0.0

    private var _recurrenceName: String = "None"
    var recurrence: Recurrence
        get() = _recurrenceName.toRecurrence()
        set(value) {
            _recurrenceName = value.name
        }

    private var _dateValue: String =
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    var date: LocalDateTime
        get() = LocalDateTime.parse(_dateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        set(value) {
            _dateValue = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

    var note: String = ""
    var category: Category? = null


    companion object {
        fun create(
            amount: Double,
            recurrence: Recurrence,
            date: LocalDateTime,
            note: String,
            category: Category
        ): Expense {
            val expense = Expense()
            expense.amount = amount
            expense.recurrence = recurrence
            expense.date = date
            expense.note = note
            expense.category = category
            return expense
        }
    }
}

data class DayExpenses(
    val expenses: MutableList<Expense>,
    var total: Double,
)

fun List<Expense>.groupedByDay(): Map<LocalDate, DayExpenses> {
    val dataMap: MutableMap<LocalDate, DayExpenses> = mutableMapOf()

    this.forEach { expense ->
        val date = expense.date.toLocalDate()

        if (dataMap[date] == null) {
            dataMap[date] = DayExpenses(
                expenses = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[date]!!.expenses.add(expense)
        dataMap[date]!!.total = dataMap[date]!!.total.plus(expense.amount)
    }

    dataMap.values.forEach { dayExpenses ->
        dayExpenses.expenses.sortBy { expense -> expense.date }
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.groupedByDayOfWeek(): Map<String, DayExpenses> {
    val dataMap: MutableMap<String, DayExpenses> = mutableMapOf()

    this.forEach { expense ->
        val dayOfWeek = expense.date.toLocalDate().dayOfWeek

        if (dataMap[dayOfWeek.name] == null) {
            dataMap[dayOfWeek.name] = DayExpenses(
                expenses = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[dayOfWeek.name]!!.expenses.add(expense)
        dataMap[dayOfWeek.name]!!.total = dataMap[dayOfWeek.name]!!.total.plus(expense.amount)
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.groupedByDayOfMonth(): Map<Int, DayExpenses> {
    val dataMap: MutableMap<Int, DayExpenses> = mutableMapOf()

    this.forEach { expense ->
        val dayOfMonth = expense.date.toLocalDate().dayOfMonth

        if (dataMap[dayOfMonth] == null) {
            dataMap[dayOfMonth] = DayExpenses(
                expenses = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[dayOfMonth]!!.expenses.add(expense)
        dataMap[dayOfMonth]!!.total = dataMap[dayOfMonth]!!.total.plus(expense.amount)
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.groupedByMonth(): Map<String, DayExpenses> {
    val dataMap: MutableMap<String, DayExpenses> = mutableMapOf()

    this.forEach { expense ->
        val month = expense.date.toLocalDate().month

        if (dataMap[month.name] == null) {
            dataMap[month.name] = DayExpenses(
                expenses = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[month.name]!!.expenses.add(expense)
        dataMap[month.name]!!.total = dataMap[month.name]!!.total.plus(expense.amount)
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.toCategorySpendingList(): List<CategorySpending> {
    val totalAmount = this.sumOf { it.amount.absoluteValue }

    return this
        .filter { it.category != null } // Ignore uncategorized
        .groupBy { it.category!! }
        .map { (category, expensesInCategory) ->
            val amount = expensesInCategory.sumOf { it.amount.absoluteValue }
            val percentage = if (totalAmount != 0.0) (amount / totalAmount).toFloat() else 0f

            CategorySpending(
                name = category.name,
                color = category.color,
                amount = amount,
                percentage = percentage,
                transactions = expensesInCategory.size
            )
        }
        .sortedByDescending { it.amount } // Optional: largest spending first
}

fun List<Expense>.filterByDateRange(start: LocalDateTime, end: LocalDateTime): List<Expense> {
    return this.filter { it.date in start..end }
}



