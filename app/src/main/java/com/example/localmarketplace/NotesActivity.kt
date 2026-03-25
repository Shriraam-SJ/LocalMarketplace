package com.example.localmarketplace

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var etNote: EditText
    private lateinit var btnAddNote: Button
    private lateinit var rvNotes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        dbHelper = DatabaseHelper(this)
        etNote = findViewById(R.id.etNote)
        btnAddNote = findViewById(R.id.btnAddNote)
        rvNotes = findViewById(R.id.rvNotes)

        setupRecyclerView()

        btnAddNote.setOnClickListener {
            val content = etNote.text.toString().trim()
            if (content.isNotEmpty()) {
                val id = dbHelper.addNote(content)
                if (id != -1L) {
                    etNote.text.clear()
                    refreshNotes()
                } else {
                    Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter(mutableListOf(), 
            onNoteUpdated = { note ->
                dbHelper.updateNote(note.id, note.content, note.isChecked)
            },
            onNoteDeleted = { note ->
                dbHelper.deleteNote(note.id)
                refreshNotes()
            }
        )
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter
        refreshNotes()
    }

    private fun refreshNotes() {
        val notes = dbHelper.getAllNotes()
        adapter.updateNotes(notes)
    }
}
