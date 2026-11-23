package com.example.notesapp.presentation.ui

import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.domain.model.Note
import com.example.notesapp.presentation.adapter.BoardActions
import com.example.notesapp.presentation.adapter.BoardAdapter
import com.example.notesapp.presentation.adapter.BoardItem
import com.example.notesapp.presentation.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), BoardActions {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: BoardAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var isInitialLoadComplete = false
    private var latestNotes: List<Note> = emptyList()
    private var knownNotes: Map<Int, Note> = emptyMap()

    /** Set when the pass submits a new note, so the arriving row is tagged NEW. */
    private var awaitingNewNote = false

    /** Non-null while the pass is editing an existing note. */
    private var editing: Note? = null

    private val cancelEditOnBack = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = exitEdit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !isInitialLoadComplete }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, cancelEditOnBack)

        setupEdgeToEdge()
        setupBoard()
        setupPass()
        setupViewModel()
    }

    // region Layout

    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val titleTop = resources.getDimensionPixelSize(R.dimen.spacing_md) - dp(4)
        val boardBottom = resources.getDimensionPixelSize(R.dimen.spacing_md)

        ViewCompat.setOnApplyWindowInsetsListener(binding.screen) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            binding.title.updateLayoutParams<androidx.constraintlayout.widget.ConstraintLayout.LayoutParams> {
                topMargin = bars.top + titleTop
            }
            binding.board.updateLayoutParams<androidx.constraintlayout.widget.ConstraintLayout.LayoutParams> {
                bottomMargin = maxOf(bars.bottom, ime.bottom) + boardBottom
            }
            insets
        }
    }

    private fun setupBoard() {
        adapter = BoardAdapter(this)
        layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
        binding.recyclerview.itemAnimator = BoardItemAnimator(dp(20).toFloat())
        lateinit var swipeHelper: ItemTouchHelper
        swipeHelper = ItemTouchHelper(
            BoardSwipeCallback(this, adapter, onDelete = ::onDelete) { note ->
                onToggleDone(note)
                // A done/reopen swipe keeps the row, but ItemTouchHelper still holds it as swiped
                // away. Re-attaching clears that state so the row draws normally in its new section.
                binding.recyclerview.post {
                    swipeHelper.attachToRecyclerView(null)
                    swipeHelper.attachToRecyclerView(binding.recyclerview)
                }
            }
        )
        swipeHelper.attachToRecyclerView(binding.recyclerview)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        viewModel.allNotes.observe(this) { list ->
            val notes = list.orEmpty()
            latestNotes = notes

            // Work out what changed so the board can light it.
            val arrived = mutableListOf<Int>()
            val changed = mutableListOf<Int>()
            if (isInitialLoadComplete) {
                notes.forEach { note ->
                    val before = knownNotes[note.id]
                    when {
                        before == null -> arrived += note.id
                        before.isPinned != note.isPinned || before.isDone != note.isDone ||
                            before.text != note.text -> changed += note.id
                    }
                }
            }
            knownNotes = notes.associateBy { it.id }

            val fresh = if (awaitingNewNote && arrived.size == 1) arrived else emptyList()
            if (fresh.isNotEmpty()) awaitingNewNote = false
            adapter.light(arrived + changed, fresh)

            val focusId = (arrived + changed).firstOrNull()
            adapter.submitList(BoardItem.build(notes)) {
                // Wait for the new layout so visibility is measured against the reordered board.
                focusId?.let { id -> binding.recyclerview.post { bringIntoView(id) } }
            }
            updateHeader(notes)
            isInitialLoadComplete = true
        }
    }

    private fun bringIntoView(noteId: Int) {
        val position = adapter.positionOf(noteId)
        if (position < 0) return
        val first = layoutManager.findFirstCompletelyVisibleItemPosition()
        val last = layoutManager.findLastCompletelyVisibleItemPosition()
        when {
            // Above the window: scroll up far enough to show the row's section label too.
            position <= first -> binding.recyclerview.smoothScrollToPosition(maxOf(0, position - 1))
            position > last -> binding.recyclerview.smoothScrollToPosition(position)
        }
    }

    private fun updateHeader(notes: List<Note>) {
        val open = notes.count { !it.isDone }
        val done = notes.count { it.isDone }
        binding.count.text = if (notes.isEmpty()) {
            getString(R.string.board_count_empty)
        } else {
            getString(R.string.board_count, open, done)
        }
        binding.emptyState.isVisible = notes.isEmpty()
        binding.recyclerview.isVisible = notes.isNotEmpty()
    }

    // endregion

    // region Pass (composer)

    private fun setupPass() {
        // Wrap long notes across lines while keeping a keyboard action key that submits.
        binding.input.setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        binding.input.setOnEditorActionListener { _, actionId, event ->
            val enter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_DONE || enter) {
                submit(); true
            } else {
                false
            }
        }
        // The stub is always lit: it is the primary action. With nothing written yet it
        // opens the pass for typing instead of doing nothing.
        binding.stub.setOnClickListener {
            if (binding.input.text.isNullOrBlank()) focusInput() else submit()
        }
        binding.passCancel.setOnClickListener { exitEdit() }
        binding.emptyState.setOnClickListener { focusInput() }
    }

    private fun submit() {
        val text = binding.input.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return

        val target = editing
        if (target != null) {
            viewModel.editNote(target, text)
            exitEdit()
            return
        }
        awaitingNewNote = true
        viewModel.addNote(text)
        printOut()
    }

    /** The note leaves the pass downward as its row drops onto the board. */
    private fun printOut() {
        val input = binding.input
        if (!Motion.enabled(this)) {
            input.text?.clear(); return
        }
        input.animate()
            .translationY(dp(14).toFloat())
            .alpha(0f)
            .setDuration(Motion.SHORT)
            .setInterpolator(Motion.standard)
            .withEndAction {
                input.text?.clear()
                input.translationY = -dp(6).toFloat()
                input.animate().translationY(0f).alpha(1f)
                    .setDuration(Motion.SHORT).setInterpolator(Motion.decelerate).start()
            }
            .start()
    }

    private fun enterEdit(note: Note) {
        editing = note
        cancelEditOnBack.isEnabled = true
        binding.passLabel.text = getString(R.string.pass_label_edit, note.id)
        binding.passFooter.setText(R.string.pass_footer_edit)
        binding.stubText.setText(R.string.pass_stub_save)
        binding.stub.contentDescription = getString(R.string.save_note)
        binding.passCancel.isVisible = true
        binding.input.setText(note.text)
        binding.input.setSelection(note.text.length)
        adapter.collapse()
        focusInput()
    }

    private fun exitEdit() {
        editing = null
        cancelEditOnBack.isEnabled = false
        binding.passLabel.setText(R.string.pass_label_new)
        binding.passFooter.setText(R.string.pass_footer_new)
        binding.stubText.setText(R.string.pass_stub_add)
        binding.stub.contentDescription = getString(R.string.add_note)
        binding.passCancel.isVisible = false
        binding.input.text?.clear()
        hideKeyboard()
    }

    private fun focusInput() {
        binding.input.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.input, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.input.windowToken, 0)
        binding.input.clearFocus()
    }

    // endregion

    // region Board actions

    override fun onEdit(note: Note) = enterEdit(note)

    override fun onTogglePin(note: Note) {
        adapter.collapse()
        viewModel.setPinned(note, !note.isPinned)
    }

    override fun onToggleDone(note: Note) {
        adapter.collapse()
        if (editing?.id == note.id) exitEdit()
        viewModel.setDone(note, !note.isDone)
    }

    override fun onDelete(note: Note) {
        adapter.forget(note.id)
        if (editing?.id == note.id) exitEdit()
        viewModel.deleteNotes(listOf(note))
        showUndo(getString(R.string.note_deleted), listOf(note))
    }

    override fun onClearDone() {
        val done = latestNotes.filter { it.isDone }
        if (done.isEmpty()) return
        done.forEach { adapter.forget(it.id) }
        viewModel.deleteNotes(done)
        showUndo(resources.getQuantityString(R.plurals.done_cleared, done.size, done.size), done)
    }

    private fun showUndo(message: String, notes: List<Note>) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.restoreNotes(notes) }
            .show()
    }

    // endregion

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
