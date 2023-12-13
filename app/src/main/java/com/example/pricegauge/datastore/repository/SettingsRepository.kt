package com.example.pricegauge.datastore.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.pricegauge.datastore.DatabaseHelper
import com.example.pricegauge.datastore.entity.Setting

class SettingsRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun createSetting(key: String, value: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, key)
            put(DatabaseHelper.COLUMN_VALUE, value)
        }

        db.insertWithOnConflict(DatabaseHelper.TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun getAllSettings(): List<Setting> {
        val db = dbHelper.readableDatabase

        val query = "SELECT * FROM ${DatabaseHelper.TABLE_SETTINGS}"
        val cursor = db.rawQuery(query, null)

        val settings = mutableListOf<Setting>()

        with(cursor) {
            while (moveToNext()) {
                val key = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val value = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_VALUE))

                val setting = Setting(key, value)
                settings.add(setting)
            }
        }

        cursor.close()
        db.close()

        return settings
    }
}
