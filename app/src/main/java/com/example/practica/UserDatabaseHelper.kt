package com.example.practica

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "login.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "usuarios"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USERNAME TEXT PRIMARY KEY, " +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTable)

        // Inserta los usuarios precargados
        insertUser(db, "admin", "admin")
        insertUser(db, "invitado", "guess")
        insertUser(db, "jaime", "jaime")
    }

    private fun insertUser(db: SQLiteDatabase?, username: String, password: String) {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        db?.insert(TABLE_USERS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun checkUserCredentials(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllUsers(): List<String> {
        val users = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_USERNAME FROM $TABLE_USERS", null)
        if (cursor.moveToFirst()) {
            do {
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                if(username != "admin"){
                    users.add(username)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }
}
