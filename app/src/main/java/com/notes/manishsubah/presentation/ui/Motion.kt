package com.notes.manishsubah.presentation.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.provider.Settings
import android.view.animation.PathInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/** One motion grammar for the board: rows move, never blink. Standard Material easing. */
object Motion {
    val decelerate = PathInterpolator(0.05f, 0.7f, 0.1f, 1f)
    val standard = PathInterpolator(0.2f, 0f, 0f, 1f)

    const val SHORT = 160L
    const val MEDIUM = 280L

    /** False when the system "Remove animations" setting is on. */
    fun enabled(context: Context): Boolean =
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
}

/**
 * New rows drop in from just above their slot while the rows below make room;
 * reorders (pin, done, reopen) slide rows to their new place. No change crossfade:
 * a row's content swaps in place and the amber light carries the change.
 */
class BoardItemAnimator(private val dropDistancePx: Float) : DefaultItemAnimator() {

    private val pendingDrops = mutableListOf<RecyclerView.ViewHolder>()

    init {
        supportsChangeAnimations = false
        moveDuration = Motion.MEDIUM
        addDuration = Motion.MEDIUM
        removeDuration = Motion.SHORT
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        endAnimation(holder)
        holder.itemView.alpha = 0f
        holder.itemView.translationY = -dropDistancePx
        pendingDrops += holder
        return true
    }

    override fun runPendingAnimations() {
        super.runPendingAnimations()
        if (pendingDrops.isEmpty()) return
        val drops = pendingDrops.toList()
        pendingDrops.clear()
        drops.forEach { holder ->
            val view = holder.itemView
            dispatchAddStarting(holder)
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(addDuration)
                .setStartDelay(moveDuration / 3)
                .setInterpolator(Motion.decelerate)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator) {
                        view.alpha = 1f; view.translationY = 0f
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        view.animate().setListener(null)
                        dispatchAddFinished(holder)
                        if (!isRunning) dispatchAnimationsFinished()
                    }
                })
                .start()
        }
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        if (pendingDrops.remove(item)) {
            item.itemView.alpha = 1f
            item.itemView.translationY = 0f
            dispatchAddFinished(item)
        }
        super.endAnimation(item)
    }

    override fun endAnimations() {
        pendingDrops.toList().forEach { endAnimation(it) }
        super.endAnimations()
    }

    override fun isRunning(): Boolean = pendingDrops.isNotEmpty() || super.isRunning()
}
