package com.example.notesapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.domain.model.Note
import com.example.notesapp.presentation.adapter.INotesRVAdapter
import com.example.notesapp.presentation.adapter.NotesRVAdapter
import com.example.notesapp.presentation.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), INotesRVAdapter {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NotesRVAdapter

    private var isInitialLoadComplete = false
    private var pendingDeleteNote: Note? = null
    private var deleteSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !isInitialLoadComplete }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupRecyclerView()
        setupViewModel()
        setupInputPanel()
    }

    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.inputPanel) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.updatePadding(
                bottom = maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }

    }

    private fun setupRecyclerView() {
        adapter = NotesRVAdapter(this)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.itemAnimator = DefaultItemAnimator()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        viewModel.allNotes.observe(this) { list ->
            val notes = list.orEmpty()
            adapter.submitList(notes)
            updateEmptyState(notes.isEmpty())
            isInitialLoadComplete = true
        }
    }

    private fun setupInputPanel() {
        binding.fabAdd.setOnClickListener { submitNote() }

        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitNote()
                true
            } else {
                false
            }
        }

        binding.emptyState.emptyStateContainer.setOnClickListener {
            binding.input.requestFocus()
            showKeyboard(binding.input)
        }
    }

    private fun submitNote() {
        val noteText = binding.input.text?.toString()?.trim().orEmpty()
        if (noteText.isEmpty()) return

        viewModel.insertNote(Note(noteText))
        binding.input.text?.clear()

        Snackbar.make(binding.inputPanel, getString(R.string.note_added), Snackbar.LENGTH_SHORT)
            .show()

        hideKeyboard()
    }

    override fun onItemClicked(note: Note) {
        pendingDeleteNote = note
        showDeleteSnackbar(note)
    }

    private fun showDeleteSnackbar(note: Note) {
        deleteSnackbar?.dismiss()

        deleteSnackbar = Snackbar.make(
            binding.inputPanel,
            getString(R.string.note_deleted),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.undo)) {
            pendingDeleteNote = null
        }.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event != DISMISS_EVENT_ACTION && pendingDeleteNote?.id == note.id) {
                    viewModel.deleteNote(note)
                }
                if (pendingDeleteNote?.id == note.id) {
                    pendingDeleteNote = null
                }
            }
        }).also { it.show() }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.emptyStateContainer.visibility =
            if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerview.visibility =
            if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.input.windowToken, 0)
    }
}
