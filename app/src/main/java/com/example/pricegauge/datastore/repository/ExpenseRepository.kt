import android.content.ContentValues
import android.content.Context
import com.example.pricegauge.PGConstants
import com.example.pricegauge.PGUtils
import com.example.pricegauge.datastore.DatabaseHelper
import com.example.pricegauge.datastore.entity.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun createExpense(amount: Double, title: String, note: String, date: Date) {
        val db = dbHelper.writableDatabase

        val currentDateTime = PGUtils.getCurrentDateString(PGConstants.SQLITE_DATETIME_FORMAT)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_TITLE, title)
            put(DatabaseHelper.COLUMN_NOTE, note)
            put(DatabaseHelper.COLUMN_DATE, PGUtils.convertDateToString(date, PGConstants.SQLITE_DATETIME_FORMAT))
            put(DatabaseHelper.COLUMN_CREATED_AT, currentDateTime)
            put(DatabaseHelper.COLUMN_UPDATED_AT, currentDateTime)
        }

        db.insert(DatabaseHelper.TABLE_EXPENSES, null, values)
        db.close()
    }

    fun getExpensesBeforeMonthYear(month: Int, year: Int): List<Expense> {
        val db = dbHelper.readableDatabase

        // Format the month and year for comparison with the SQLite date format
        val targetMonthYear = String.format("%04d-%02d", year, month + 1)

        val query = """
        SELECT * FROM ${DatabaseHelper.TABLE_EXPENSES}
        WHERE strftime('%Y-%m', ${DatabaseHelper.COLUMN_DATE}) < ?
        ORDER BY ${DatabaseHelper.COLUMN_DATE} ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(targetMonthYear))

        val expenses = mutableListOf<Expense>()

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val note = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE))
                val date = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                val createdAt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                val updatedAt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))

                val expense = Expense(id, amount, title, note, date, createdAt, updatedAt)
                expenses.add(expense)
            }
        }

        cursor.close()
        db.close()

        return expenses
    }

    fun getMonthExpenses(month: Int, year: Int): List<Expense> {
        val db = dbHelper.readableDatabase

        val monthYear = SimpleDateFormat("yyyy-MM").format(GregorianCalendar(year, month, 1).time)
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_EXPENSES} WHERE substr(${DatabaseHelper.COLUMN_DATE}, 1, 7) = ?"
        val cursor = db.rawQuery(query, arrayOf(monthYear))

        val expenses = mutableListOf<Expense>()

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val note = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE))
                val date = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                val createdAt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                val updatedAt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))

                val expense = Expense(id, amount, title, note, date, createdAt, updatedAt)
                expenses.add(expense)
            }
        }

        cursor.close()
        db.close()

        return expenses
    }
}
