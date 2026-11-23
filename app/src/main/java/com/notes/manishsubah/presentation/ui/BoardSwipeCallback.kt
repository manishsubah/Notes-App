package com.notes.manishsubah.presentation.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.notes.manishsubah.R
import com.notes.manishsubah.domain.model.Note
import com.notes.manishsubah.presentation.adapter.BoardAdapter
import com.notes.manishsubah.presentation.adapter.BoardItem
import java.util.Locale

/**
 * Swipe a note row: toward the start deletes it, toward the end marks it done (or reopens it).
 * The revealed strip names the action in the board's label voice.
 */
class BoardSwipeCallback(
    context: Context,
    private val adapter: BoardAdapter,
    private val onDelete: (Note) -> Unit,
    private val onToggleDone: (Note) -> Unit,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

    private val density = context.resources.displayMetrics.density
    private val dangerColor = ContextCompat.getColor(context, R.color.danger)
    private val doneColor = ContextCompat.getColor(context, R.color.ink)
    private val bgPaint = Paint()
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 13f * context.resources.displayMetrics.scaledDensity
        typeface = ResourcesCompat.getFont(context, R.font.barlow_semi_condensed_bold)
        letterSpacing = 0.08f
    }
    private val labelOnDanger = ContextCompat.getColor(context, R.color.on_danger)
    private val labelOnDone = ContextCompat.getColor(context, R.color.panel)
    private val deleteLabel = context.getString(R.string.swipe_delete).uppercase(Locale.getDefault())
    private val doneLabel = context.getString(R.string.swipe_done).uppercase(Locale.getDefault())
    private val reopenLabel = context.getString(R.string.swipe_reopen).uppercase(Locale.getDefault())

    private fun noteAt(holder: RecyclerView.ViewHolder): Note? {
        val position = holder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return null
        return (adapter.currentList.getOrNull(position) as? BoardItem.Row)?.note
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder): Int =
        if (noteAt(holder) == null) 0 else super.getSwipeDirs(recyclerView, holder)

    override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = 0.4f

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        val note = noteAt(holder) ?: return
        if (direction == ItemTouchHelper.START) {
            onDelete(note)
        } else {
            // The row stays on the board (it moves sections), so the swipe must be undone visually.
            holder.itemView.translationX = 0f
            onToggleDone(note)
        }
    }

    override fun onChildDraw(
        c: Canvas, rv: RecyclerView, holder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,
    ) {
        val note = noteAt(holder)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && note != null && dX != 0f) {
            val v = holder.itemView
            val pad = 20f * density
            val baseline = v.top + v.height / 2f - (labelPaint.descent() + labelPaint.ascent()) / 2f
            if (dX < 0) {
                bgPaint.color = dangerColor
                c.drawRect(v.right + dX, v.top.toFloat(), v.right.toFloat(), v.bottom.toFloat(), bgPaint)
                labelPaint.color = labelOnDanger
                labelPaint.textAlign = Paint.Align.RIGHT
                c.drawText(deleteLabel, v.right - pad, baseline, labelPaint)
            } else {
                bgPaint.color = doneColor
                c.drawRect(v.left.toFloat(), v.top.toFloat(), v.left + dX, v.bottom.toFloat(), bgPaint)
                labelPaint.color = labelOnDone
                labelPaint.textAlign = Paint.Align.LEFT
                c.drawText(if (note.isDone) reopenLabel else doneLabel, v.left + pad, baseline, labelPaint)
            }
        }
        super.onChildDraw(c, rv, holder, dX, dY, actionState, isCurrentlyActive)
    }
}
