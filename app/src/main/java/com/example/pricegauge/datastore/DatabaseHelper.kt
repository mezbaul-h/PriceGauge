package com.example.pricegauge.datastore

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "primary_database.db"
        const val DATABASE_VERSION = 3

        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DATE = "date"
        const val COLUMN_TITLE = "title"
        const val COLUMN_NOTE = "note"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"

        const val TABLE_SETTINGS = "settings"
        const val COLUMN_NAME = "name"
        const val COLUMN_VALUE = "value"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_TITLE TEXT,
                $COLUMN_NOTE TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_CREATED_AT TEXT,
                $COLUMN_UPDATED_AT TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)

        val createSettingsTableQuery = """
            CREATE TABLE $TABLE_SETTINGS (
                $COLUMN_NAME TEXT PRIMARY KEY,
                $COLUMN_VALUE TEXT
            )
        """.trimIndent()

        db.execSQL(createSettingsTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Implement database upgrade if needed
        // Upgrade logic for each version increment
        when (oldVersion) {
            1 -> {
                // Upgrade from version 1 to 2
                // Add a new column to the existing table
                //db.execSQL("ALTER TABLE $TABLE_EXPENSES ADD COLUMN $COLUMN_DATE TEXT")
            }
            // Add cases for other versions if needed
        }
    }
}
