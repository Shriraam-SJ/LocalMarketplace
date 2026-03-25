package com.example.localmarketplace

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private var notes: MutableList<Note>,
    private val onNoteUpdated: (Note) -> Unit,
    private val onNoteDeleted: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbNote: CheckBox = view.findViewById(R.id.cbNote)
        val etNoteItem: EditText = view.findViewById(R.id.etNoteItem)
        val btnDeleteNote: ImageButton = view.findViewById(R.id.btnDeleteNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        
        holder.etNoteItem.setText(note.content)
        holder.cbNote.isChecked = note.isChecked
        
        updateStrikeThrough(holder.etNoteItem, note.isChecked)

        holder.cbNote.setOnCheckedChangeListener { _, isChecked ->
            note.isChecked = isChecked
            updateStrikeThrough(holder.etNoteItem, isChecked)
            onNoteUpdated(note)
        }

        holder.btnDeleteNote.setOnClickListener {
            onNoteDeleted(note)
        }

        holder.etNoteItem.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                note.content = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                onNoteUpdated(note)
            }
        })
    }

    override fun getItemCount() = notes.size

    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    private fun updateStrikeThrough(editText: EditText, isChecked: Boolean) {
        if (isChecked) {
            editText.paintFlags = editText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            editText.setTextColor(editText.context.getColor(R.color.grey))
        } else {
            editText.paintFlags = editText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            editText.setTextColor(editText.context.getColor(R.color.black))
        }
    }
}
