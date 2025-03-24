// SmsExpenseRepository.kt
package com.example.money.data.repository

import android.content.Context
import android.net.Uri
import com.example.money.data.db
import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence
import com.example.money.utils.SmsParser
import com.example.money.utils.mapMerchantToCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

class SmsExpenseRepository(private val context: Context) : ExpenseRepository {
    override suspend fun readSmsExpenses(): List<Expense> = withContext(Dispatchers.IO) {
        val expenses = mutableListOf<Expense>()
        val uri: Uri = "content://sms/inbox".toUri()
        val projection = arrayOf("_id", "address", "date", "body")

        val cursor = context.contentResolver.query(uri, projection, null, null, "date DESC")
        cursor?.use {
            val indexBody = it.getColumnIndex("body")
            while (it.moveToNext()) {
                val smsBody = it.getString(indexBody)
                val parsed = SmsParser.parseSms(smsBody)
                val expense = parsed?.date?.let { date ->
                    Expense.create(
                        amount = if (parsed.type == "credit") parsed.amount else -parsed.amount,
                        date = date.atStartOfDay(),
                        recurrence = Recurrence.None,
                        note = parsed.merchant,
                        category = mapMerchantToCategory(parsed.merchant, db)
                    )
                }
                if (expense != null) expenses.add(expense)
            }
        }
        expenses
    }
}
