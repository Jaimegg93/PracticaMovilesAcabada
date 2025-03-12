package com.example.practica

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class MountainDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Definimos constantes para los nombres de la base de datos, tablas y columnas
    companion object {
        private const val DATABASE_NAME = "montanas.db" // Nombre de la base de datos
        private const val DATABASE_VERSION = 1 // Versión de la base de datos (si cambias esto, se ejecuta onUpgrade())
        private const val TABLE_MOUNTAINS = "montanas" // Nombre de la tabla
        private const val COLUMN_NOMBRE = "nombre" // Nombre de la montaña
        private const val COLUMN_USUARIO = "usuario" // Usuario que añadió la montaña
        private const val COLUMN_ALTURA = "altura" // Altura de la montaña
        private const val COLUMN_CONCEJO = "concejo"
    }

    // Método que se ejecuta al crear la base de datos por primera vez
    override fun onCreate(db: SQLiteDatabase?) {
        // Consulta para crear la tabla de montañas
        val createTable = "CREATE TABLE $TABLE_MOUNTAINS (" +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_USUARIO TEXT, " +
                "$COLUMN_ALTURA INTEGER, " +
                "$COLUMN_CONCEJO TEXT, " +
                "PRIMARY KEY($COLUMN_NOMBRE, $COLUMN_USUARIO))"
        db?.execSQL(createTable) // Ejecuta la consulta para crear la tabla
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOUNTAINS") // Elimina la tabla existente
        onCreate(db) // Vuelve a crear la tabla con la nueva estructura
    }

    fun comporbarMontañaExistente(nombre: String, usuario: String): Boolean {
        // Obtiene la lista de montañas para ese usuario
        val montañas = getMountainsForUser(usuario, false)

        // Recorre la lista para comprobar si ya existe una montaña con el mismo nombre (sin importar mayúsculas/minúsculas)
        for (montaña in montañas) {
            if (montaña.nombre.equals(nombre, ignoreCase = true)) {
                return true // Devuelve true si encuentra una coincidencia
            }
        }
        return false // Devuelve false si no encuentra coincidencias
    }

    fun insertMountain(nombre: String, usuario: String, altura: Int, concejo: String): Boolean {
        val db = writableDatabase // Abre la base de datos en modo escritura

        // Crea un conjunto de valores para insertar en la base de datos
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_USUARIO, usuario)
            put(COLUMN_ALTURA, altura)
            put(COLUMN_CONCEJO, concejo)
        }

        // Inserta los datos en la base de datos
        val result = db.insert(TABLE_MOUNTAINS, null, values)
        return result != -1L // Devuelve true si la inserción fue exitosa
    }

    fun updateMountain(nombre: String, usuario: String, altura: Int, concejo: String): Boolean {
        val db = writableDatabase

        // Crea un conjunto de valores para actualizar la montaña
        val values = ContentValues().apply {
            put(COLUMN_ALTURA, altura) // Solo actualiza la altura
            put(COLUMN_CONCEJO, concejo)
        }

        // Ejecuta la actualización
        val result = db.update(
            TABLE_MOUNTAINS,
            values,
            "$COLUMN_NOMBRE = ? AND $COLUMN_USUARIO = ?", // Condición para identificar la montaña
            arrayOf(nombre, usuario) // Parámetros para la condición
        )
        return result > 0 // Devuelve true si se actualizó correctamente
    }

    // ✅ Método para eliminar una montaña de la base de datos
    fun deleteMountain(nombre: String, usuario: String): Boolean {
        val db = writableDatabase

        // Ejecuta la eliminación
        val result = db.delete(
            TABLE_MOUNTAINS,
            "$COLUMN_NOMBRE = ? AND $COLUMN_USUARIO = ?", // Condición para eliminar la montaña específica
            arrayOf(nombre, usuario) // Parámetros para la condición
        )
        return result > 0 // Devuelve true si se eliminó correctamente
    }

    fun getMountainsForUser(currentUser: String, isAdmin: Boolean): List<Mountain> {
        val mountains = mutableListOf<Mountain>() // Lista para almacenar las montañas
        val db = readableDatabase // Abre la base de datos en modo lectura

        // Consulta diferente si el usuario es administrador o no
        val query = if (isAdmin) {
            "SELECT * FROM $TABLE_MOUNTAINS" // Si es admin, obtiene todas las montañas
        } else {
            "SELECT * FROM $TABLE_MOUNTAINS WHERE $COLUMN_USUARIO = ?" // Si no es admin, solo las del usuario actual
        }

        // Ejecuta la consulta SQL
        val cursor = if (isAdmin) {
            db.rawQuery(query, null)
        } else {
            db.rawQuery(query, arrayOf(currentUser))
        }

        // Recorre los resultados obtenidos y los añade a la lista
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)) // Obtiene el nombre
                val usuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO)) // Obtiene el usuario
                val altura = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALTURA)) // Obtiene la altura
                val concejo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONCEJO)) // Obtiene la altura

                // Crea un objeto Mountain y lo añade a la lista
                val mountain = Mountain(nombre, usuario, altura, concejo)
                mountains.add(mountain)
            } while (cursor.moveToNext())
        }

        cursor.close() // Cierra el cursor para evitar fugas de memoria
        return mountains // Devuelve la lista de montañas
    }
}