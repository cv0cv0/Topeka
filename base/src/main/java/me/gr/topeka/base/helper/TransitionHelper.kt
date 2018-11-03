package me.gr.topeka.base.helper

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View
import androidx.annotation.IdRes
import androidx.core.util.Pair

object TransitionHelper {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun createSafeTransitionParticipants(
        activity: Activity,
        includeStatusBar: Boolean,
        vararg others: Pair<View, String>
    ) = ArrayList<Pair<View, String>>(3).apply {
        if (includeStatusBar) {
            addViewById(activity, android.R.id.statusBarBackground, this)
        }
        addViewById(activity, android.R.id.navigationBarBackground, this)
        addAll(others)
    }.toTypedArray()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addViewById(
        activity: Activity,
        @IdRes id: Int,
        participants: ArrayList<Pair<View, String>>
    ) = activity.window.decorView.findViewById<View>(id)?.run {
        participants.add(Pair(this, transitionName))
    }
}