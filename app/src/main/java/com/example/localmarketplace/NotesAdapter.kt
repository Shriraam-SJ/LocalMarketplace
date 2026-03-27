package com.example.localmarketplace

import android.graphics.Paint
import android.media.MediaPlayer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class NotesAdapter(
    private var notes: MutableList<Note>,
    private val onNoteUpdated: (Note) -> Unit,
    private val onNoteDeleted: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingId: Int = -1

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbNote: CheckBox = view.findViewById(R.id.cbNote)
        val etNoteItem: EditText = view.findViewById(R.id.etNoteItem)
        val btnDeleteNote: ImageButton = view.findViewById(R.id.btnDeleteNote)
        val layoutAudio: LinearLayout = view.findViewById(R.id.layoutAudio)
        val btnPlayPause: ImageButton = view.findViewById(R.id.btnPlayPause)
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

        // Audio Visibility
        if (!note.audioPath.isNullOrEmpty()) {
            holder.layoutAudio.visibility = View.VISIBLE
            
            // Set correct icon if this is the one playing
            if (currentlyPlayingId == note.id) {
                holder.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                holder.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            }

            holder.btnPlayPause.setOnClickListener {
                if (currentlyPlayingId == note.id) {
                    stopPlaying()
                } else {
                    startPlaying(note.audioPath!!, note.id)
                }
            }
        } else {
            holder.layoutAudio.visibility = View.GONE
        }

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

    private fun startPlaying(path: String, id: Int) {
        stopPlaying()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(path)
                prepare()
                start()
                currentlyPlayingId = id
                notifyDataSetChanged()
                setOnCompletionListener {
                    stopPlaying()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentlyPlayingId = -1
        notifyDataSetChanged()
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
