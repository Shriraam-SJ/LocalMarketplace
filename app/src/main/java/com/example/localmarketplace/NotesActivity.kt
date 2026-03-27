package com.example.localmarketplace

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.IOException

class NotesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var etNote: EditText
    private lateinit var btnAddNote: Button
    private lateinit var btnRecordVoice: FloatingActionButton
    private lateinit var rvNotes: RecyclerView

    private var mediaRecorder: MediaRecorder? = null
    private var audioPath: String? = null
    private var isRecording = false

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        dbHelper = DatabaseHelper(this)
        etNote = findViewById(R.id.etNote)
        btnAddNote = findViewById(R.id.btnAddNote)
        btnRecordVoice = findViewById(R.id.btnRecordVoice)
        rvNotes = findViewById(R.id.rvNotes)

        setupRecyclerView()

        btnAddNote.setOnClickListener {
            val content = etNote.text.toString().trim()
            if (content.isNotEmpty() || audioPath != null) {
                val id = dbHelper.addNote(content, audioPath)
                if (id != -1L) {
                    etNote.text.clear()
                    audioPath = null
                    refreshNotes()
                } else {
                    Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter text or record audio", Toast.LENGTH_SHORT).show()
            }
        }

        btnRecordVoice.setOnClickListener {
            if (checkPermissions()) {
                if (isRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
            } else {
                requestPermissions()
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

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecording()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startRecording() {
        val file = File(externalCacheDir, "audio_note_${System.currentTimeMillis()}.mp4")
        audioPath = file.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioPath)
            try {
                prepare()
                start()
                isRecording = true
                btnRecordVoice.setImageResource(android.R.drawable.ic_media_pause)
                btnRecordVoice.backgroundTintList = ContextCompat.getColorStateList(this@NotesActivity, android.R.color.holo_red_light)
                Toast.makeText(this@NotesActivity, "Recording started...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaRecorder = null
        isRecording = false
        btnRecordVoice.setImageResource(android.R.drawable.ic_btn_speak_now)
        btnRecordVoice.backgroundTintList = ContextCompat.getColorStateList(this, R.color.light_orange)
        Toast.makeText(this, "Recording saved! Click 'Add' to save note.", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            stopRecording()
        }
    }
}
