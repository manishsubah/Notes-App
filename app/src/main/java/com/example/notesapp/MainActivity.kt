package com.example.notesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.InvalidationTracker.Observer
import com.example.notesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), INotesRVAdapter {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = NotesRVAdapter(this, this)
        binding.recyclerview.adapter = adapter

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel :: class.java)
        viewModel.allNotes.observe(this, androidx.lifecycle.Observer {list->

            list?.let {
                adapter.updateList(it)
            }

        })



    }

    override fun onItemClicked(note: Note) {
        viewModel.deleteNote(note)
        Toast.makeText(this, "${note.text} deleted.", Toast.LENGTH_LONG).show()
    }

    fun submitData(view: View) {
        val noteText = binding.input.text.toString()
        if(noteText.isNotEmpty()) {
            viewModel.insertNote(Note(noteText))

            Toast.makeText(this, "$noteText inserted.", Toast.LENGTH_LONG).show()
            binding.input.setText("")
        }
    }
}