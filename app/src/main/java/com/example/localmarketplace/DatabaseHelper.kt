package com.example.localmarketplace

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LocalMarketplace.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NOTES = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_IS_CHECKED = "is_checked"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_IS_CHECKED + " INTEGER DEFAULT 0" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES)
        onCreate(db)
    }

    fun addNote(content: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTENT, content)
        values.put(COLUMN_IS_CHECKED, 0)
        return db.insert(TABLE_NOTES, null, values)
    }

    fun updateNote(id: Int, content: String, isChecked: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTENT, content)
        values.put(COLUMN_IS_CHECKED, if (isChecked) 1 else 0)
        db.update(TABLE_NOTES, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteNote(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES ORDER BY $COLUMN_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED)) == 1
                notes.add(Note(id, content, isChecked))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }
}

data class Note(val id: Int, var content: String, var isChecked: Boolean)
