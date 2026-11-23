package com.example.notesapp.presentation.adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.databinding.ItemNoteBinding
import com.example.notesapp.databinding.ItemSectionBinding
import com.example.notesapp.domain.model.Note
import com.example.notesapp.presentation.ui.Motion
import java.util.Calendar
import java.util.Locale

/** One line on the board: a section label or a note row. */
sealed class BoardItem {
    abstract val key: Long

    data class Section(val kind: Kind, val count: Int) : BoardItem() {
        enum class Kind { PINNED, OPEN, DONE }
        override val key: Long = -1L - kind.ordinal
    }

    data class Row(val note: Note) : BoardItem() {
        override val key: Long = note.id.toLong()
    }

    companion object {
        /** Notes arrive already in board order (see NoteDao); this inserts the section labels. */
        fun build(notes: List<Note>): List<BoardItem> {
            val pinned = notes.filter { !it.isDone && it.isPinned }
            val open = notes.filter { !it.isDone && !it.isPinned }
            val done = notes.filter { it.isDone }
            return buildList {
                if (pinned.isNotEmpty()) {
                    add(Section(Section.Kind.PINNED, pinned.size)); pinned.forEach { add(Row(it)) }
                }
                if (open.isNotEmpty()) {
                    add(Section(Section.Kind.OPEN, open.size)); open.forEach { add(Row(it)) }
                }
                if (done.isNotEmpty()) {
                    add(Section(Section.Kind.DONE, done.size)); done.forEach { add(Row(it)) }
                }
            }
        }
    }
}

/** What a status tag shows: label, tile background (0 for none) and text colour. */
private data class StatusLook(val label: Int, val background: Int, val color: Int)

interface BoardActions {
    fun onEdit(note: Note)
    fun onTogglePin(note: Note)
    fun onToggleDone(note: Note)
    fun onDelete(note: Note)
    fun onClearDone()
}

class BoardAdapter(
    private val actions: BoardActions,
) : ListAdapter<BoardItem, RecyclerView.ViewHolder>(Diff) {

    /** Notes whose row is lit amber because they just changed; cleared once seen. */
    private val litIds = mutableSetOf<Int>()

    /** Notes added during this session, tagged NEW until they are touched. */
    private val freshIds = mutableSetOf<Int>()

    var expandedId: Int? = null
        private set

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).key

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is BoardItem.Section -> TYPE_SECTION
        is BoardItem.Row -> TYPE_ROW
    }

    fun light(ids: Collection<Int>, fresh: Collection<Int> = emptyList()) {
        if (ids.isEmpty() && fresh.isEmpty()) return
        litIds += ids
        freshIds += fresh
        ids.forEach { id -> positionOf(id).takeIf { it >= 0 }?.let { notifyItemChanged(it, PAYLOAD_LIT) } }
    }

    fun forget(id: Int) {
        freshIds -= id
        litIds -= id
        if (expandedId == id) expandedId = null
    }

    fun toggleExpanded(id: Int) {
        val previous = expandedId
        expandedId = if (previous == id) null else id
        freshIds -= id
        previous?.let { positionOf(it).takeIf { p -> p >= 0 }?.let(::notifyItemChanged) }
        if (expandedId != null) positionOf(id).takeIf { it >= 0 }?.let(::notifyItemChanged)
    }

    fun collapse() {
        expandedId?.let { id -> expandedId = null; positionOf(id).takeIf { it >= 0 }?.let(::notifyItemChanged) }
    }

    fun positionOf(noteId: Int): Int =
        currentList.indexOfFirst { it is BoardItem.Row && it.note.id == noteId }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_SECTION) {
            SectionHolder(ItemSectionBinding.inflate(inflater, parent, false))
        } else {
            RowHolder(ItemNoteBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is BoardItem.Section -> (holder as SectionHolder).bind(item)
            is BoardItem.Row -> (holder as RowHolder).bind(item.note)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        if (payloads.isNotEmpty() && payloads.all { it == PAYLOAD_LIT } && holder is RowHolder && item is BoardItem.Row) {
            holder.bindLight(item.note)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        (holder as? RowHolder)?.let { it.cancelLight(); it.stopFlap() }
    }

    inner class SectionHolder(private val b: ItemSectionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(section: BoardItem.Section) {
            val ctx = b.root.context
            val name = when (section.kind) {
                BoardItem.Section.Kind.PINNED -> ctx.getString(R.string.section_pinned)
                BoardItem.Section.Kind.OPEN -> ctx.getString(R.string.section_open)
                BoardItem.Section.Kind.DONE -> ctx.getString(R.string.section_done)
            }
            b.sectionLabel.text = String.format(Locale.getDefault(), "%s · %d", name, section.count)
            b.sectionAction.isVisible = section.kind == BoardItem.Section.Kind.DONE
            b.sectionAction.setOnClickListener { actions.onClearDone() }
        }
    }

    inner class RowHolder(private val b: ItemNoteBinding) : RecyclerView.ViewHolder(b.root) {

        private var fade: ValueAnimator? = null
        private val fadeOut = Runnable { startFade() }
        private var boundId = -1
        private var boundNote: Note? = null
        private var shownLook: StatusLook? = null
        private var shownForId = -1

        fun bind(note: Note) {
            val ctx = b.root.context
            boundId = note.id
            boundNote = note
            val expanded = expandedId == note.id

            b.time.text = formatTime(ctx, note.createdAt)
            b.text.text = note.text
            b.text.maxLines = if (expanded) Int.MAX_VALUE else 2
            b.text.setTextColor(ContextCompat.getColor(ctx, if (note.isDone) R.color.text_done else R.color.ink))
            b.text.paintFlags = if (note.isDone) {
                b.text.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                b.text.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            val status = bindStatus(ctx, note)

            // Expanded detail: meta line and actions.
            b.meta.isVisible = expanded
            b.actions.isVisible = expanded
            b.actionsBottom.isVisible = expanded
            listOf(b.actionEdit, b.actionPin, b.actionDone, b.actionDelete).forEach { it.isVisible = expanded }
            if (expanded) {
                b.meta.text = metaLine(ctx, note)
                b.actionEdit.isVisible = !note.isDone
                b.actionPin.isVisible = !note.isDone
                b.actionPin.setText(if (note.isPinned) R.string.action_unpin else R.string.action_pin)
                b.actionDone.setText(if (note.isDone) R.string.action_reopen else R.string.action_done)
                b.actionDone.setIconResource(if (note.isDone) R.drawable.ic_reopen else R.drawable.ic_check)
                b.actionEdit.setOnClickListener { actions.onEdit(note) }
                b.actionPin.setOnClickListener { actions.onTogglePin(note) }
                b.actionDone.setOnClickListener { actions.onToggleDone(note) }
                b.actionDelete.setOnClickListener { actions.onDelete(note) }
            }

            b.rowHit.setOnClickListener { toggleExpanded(note.id) }
            b.rowHit.contentDescription = ctx.getString(R.string.row_a11y, status, note.text)
            b.rowHit.isActivated = expanded

            bindLight(note)
        }

        private fun bindStatus(ctx: Context, note: Note): String {
            val look = when {
                note.isDone -> StatusLook(R.string.status_done, 0, R.color.text_done)
                note.id in freshIds -> StatusLook(R.string.status_new, R.drawable.bg_tag_new, R.color.on_signal)
                note.isPinned -> StatusLook(R.string.status_pinned, R.drawable.bg_tag_pinned, R.color.panel)
                else -> StatusLook(R.string.status_open, 0, R.color.label)
            }
            val previous = shownLook.takeIf { shownForId == note.id }
            shownLook = look
            shownForId = note.id
            if (previous != null && previous != look && Motion.enabled(ctx)) {
                flap(ctx, look)
            } else {
                stopFlap()
                applyLook(ctx, look)
            }
            return ctx.getString(look.label)
        }

        private fun applyLook(ctx: Context, look: StatusLook) {
            b.status.setText(look.label)
            if (look.background == 0) b.status.background = null else b.status.setBackgroundResource(look.background)
            b.status.setTextColor(ContextCompat.getColor(ctx, look.color))
        }

        /**
         * Split-flap change: the tag folds away, a blank tile flips through, then the new
         * status flips down and settles. The row never blinks; only its status turns over.
         */
        private fun flap(ctx: Context, to: StatusLook) {
            val tag = b.status
            stopFlap()
            tag.cameraDistance = 8000f * ctx.resources.displayMetrics.density
            tag.pivotY = tag.height / 2f
            fold(tag, FLAP_HALF_MS) {
                // Blank tile, already at the new label's width so the column never jumps.
                tag.setText(to.label)
                tag.setBackgroundResource(R.drawable.bg_tag_blank)
                tag.setTextColor(android.graphics.Color.TRANSPARENT)
                unfold(tag, FLAP_HALF_MS, Motion.standard) {
                    fold(tag, FLAP_HALF_MS) {
                        applyLook(ctx, to)
                        unfold(tag, FLAP_SETTLE_MS, Motion.decelerate) {}
                    }
                }
            }
        }

        private fun fold(tag: View, ms: Long, then: () -> Unit) {
            tag.animate().rotationX(90f).setDuration(ms).setInterpolator(flapIn).withEndAction(then).start()
        }

        private fun unfold(tag: View, ms: Long, curve: android.animation.TimeInterpolator, then: () -> Unit) {
            tag.rotationX = -90f
            tag.animate().rotationX(0f).setDuration(ms).setInterpolator(curve).withEndAction(then).start()
        }

        fun stopFlap() {
            b.status.animate().cancel()
            b.status.rotationX = 0f
        }

        fun bindLight(note: Note) {
            cancelLight()
            val ctx = b.root.context
            val base = ContextCompat.getColor(ctx, R.color.panel)
            if (note.id in litIds) {
                b.row.setBackgroundColor(ContextCompat.getColor(ctx, R.color.signal_lit_row))
                // Hold the light long enough to be noticed, then let it go.
                b.row.postDelayed(fadeOut, LIT_HOLD_MS)
            } else {
                b.row.setBackgroundColor(base)
            }
        }

        private fun startFade() {
            val ctx = b.root.context
            val from = ContextCompat.getColor(ctx, R.color.signal_lit_row)
            val to = ContextCompat.getColor(ctx, R.color.panel)
            litIds -= boundId
            // Amber marks only a live change: once the light goes, NEW settles to the row's status.
            if (freshIds.remove(boundId)) boundNote?.let { bindStatus(ctx, it) }
            if (!Motion.enabled(ctx)) {
                b.row.setBackgroundColor(to); return
            }
            fade = ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
                duration = LIT_FADE_MS
                interpolator = Motion.decelerate
                addUpdateListener { b.row.setBackgroundColor(it.animatedValue as Int) }
                start()
            }
        }

        fun cancelLight() {
            b.row.removeCallbacks(fadeOut)
            fade?.cancel()
            fade = null
        }
    }

    private fun formatTime(ctx: Context, millis: Long): String {
        if (millis <= 0L) return ctx.getString(R.string.time_earlier).uppercase(Locale.getDefault())
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { timeInMillis = millis }
        val sameDay = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
        return when {
            sameDay -> DateFormat.format(if (DateFormat.is24HourFormat(ctx)) "HH:mm" else "h:mm a", millis).toString()
            System.currentTimeMillis() - millis < 6 * DateUtils.DAY_IN_MILLIS ->
                DateFormat.format("EEE", millis).toString()
            else -> DateFormat.format("d MMM", millis).toString()
        }.uppercase(Locale.getDefault())
    }

    private fun stamp(ctx: Context, millis: Long): String =
        DateUtils.formatDateTime(
            ctx, millis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_ABBREV_ALL
        )

    private fun metaLine(ctx: Context, note: Note): String = when {
        note.createdAt <= 0L -> ctx.getString(R.string.row_meta_legacy, note.id)
        note.updatedAt - note.createdAt > 60_000L ->
            ctx.getString(R.string.row_meta_edited, note.id, stamp(ctx, note.createdAt), stamp(ctx, note.updatedAt))
        else -> ctx.getString(R.string.row_meta, note.id, stamp(ctx, note.createdAt))
    }

    companion object {
        const val TYPE_SECTION = 0
        const val TYPE_ROW = 1
        private const val PAYLOAD_LIT = "lit"
        private const val LIT_HOLD_MS = 2200L
        private const val LIT_FADE_MS = 900L
        private const val FLAP_HALF_MS = 70L
        private const val FLAP_SETTLE_MS = 130L
        private val flapIn = android.view.animation.AccelerateInterpolator(1.5f)

        private val Diff = object : DiffUtil.ItemCallback<BoardItem>() {
            override fun areItemsTheSame(oldItem: BoardItem, newItem: BoardItem) = oldItem.key == newItem.key
            override fun areContentsTheSame(oldItem: BoardItem, newItem: BoardItem) = oldItem == newItem
        }
    }
}
