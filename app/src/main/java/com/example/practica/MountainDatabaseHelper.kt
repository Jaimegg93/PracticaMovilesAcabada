package com.example.practica

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MountainDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "montanas.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MOUNTAINS = "montanas"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_USUARIO = "usuario"
        private const val COLUMN_ALTURA = "altura"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_MOUNTAINS (" +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_USUARIO TEXT, " +
                "$COLUMN_ALTURA INTEGER, " +
                "PRIMARY KEY($COLUMN_NOMBRE, $COLUMN_USUARIO))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOUNTAINS")
        onCreate(db)
    }

    fun insertMountain(nombre: String, usuario: String, altura: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_USUARIO, usuario)
            put(COLUMN_ALTURA, altura)
        }
        val result = db.insert(TABLE_MOUNTAINS, null, values)
        return result != -1L
    }

    fun updateMountain(nombre: String, usuario: String, altura: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALTURA, altura)
        }
        val result = db.update(
            TABLE_MOUNTAINS,
            values,
            "$COLUMN_NOMBRE = ? AND $COLUMN_USUARIO = ?",
            arrayOf(nombre, usuario)
        )
        return result > 0
    }

    fun deleteMountain(nombre: String, usuario: String): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLE_MOUNTAINS,
            "$COLUMN_NOMBRE = ? AND $COLUMN_USUARIO = ?",
            arrayOf(nombre, usuario)
        )
        return result > 0
    }

    fun getMountainsForUser(currentUser: String, isAdmin: Boolean): List<Mountain> {
        val mountains = mutableListOf<Mountain>()
        val db = readableDatabase
        val query = if (isAdmin) {
            "SELECT * FROM $TABLE_MOUNTAINS"
        } else {
            "SELECT * FROM $TABLE_MOUNTAINS WHERE $COLUMN_USUARIO = ?"
        }
        val cursor = if (isAdmin) {
            db.rawQuery(query, null)
        } else {
            db.rawQuery(query, arrayOf(currentUser))
        }
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val usuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO))
                val altura = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALTURA))
                val mountain = Mountain(nombre, usuario, altura)
                mountains.add(mountain)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return mountains
    }
}
