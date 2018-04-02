package moviester.ornoyman.com.moviester.presentation.utils

import android.support.v7.widget.RecyclerView
import io.reactivex.functions.Action

/**
 * Allows safely updating the backing data and notifying recycler view.
 * If adapter is currently in lockdown state, will post update action to avoid illegal state error.
 * Saves and restores scrolling state so hopefully updates will not result in
 * the dreaded 'Inconsistency detected' error.
 */
fun RecyclerView.safeUpdate(update: Action) = this.safeUpdate(update::run)

/**
 * Allows safely updating the backing data and notifying recycler view.
 * If adapter is currently in lockdown state, will post update action to avoid illegal state error.
 * Saves and restores scrolling state so hopefully updates will not result in
 * the dreaded 'Inconsistency detected' error.
 */
inline fun RecyclerView.safeUpdate(crossinline update: () -> Unit) {
    val safeUpdate = createSafeUpdate(update)
    if (!isComputingLayout) safeUpdate() else post { safeUpdate() }
}

inline fun RecyclerView.createSafeUpdate(crossinline update: () -> Unit): () -> Unit {
    return {
        val state = layoutManager?.onSaveInstanceState()
        update()
        layoutManager?.onRestoreInstanceState(state)
    }
}