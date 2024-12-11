package com.example.schoolalert

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE schedules (id INTEGER PRIMARY KEY, subject TEXT, time TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS schedules")
        onCreate(db)
    }

    fun insertSchedule(subject: String, time: String) {
        val db = writableDatabase
        val query = "INSERT INTO schedules (subject, time) VALUES ('$subject', '$time')"
        db.execSQL(query)
        db.close()
    }

    fun getAllSchedules(): List<Schedule> {
        val schedules = mutableListOf<Schedule>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM schedules", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val subject = cursor.getString(cursor.getColumnIndex("subject"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                schedules.add(Schedule(id, subject, time))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return schedules
    }

    companion object {
        const val DATABASE_NAME = "school_alert.db"
        const val DATABASE_VERSION = 1
    }
}

data class Schedule(val id: Int, val subject: String, val time: String)
