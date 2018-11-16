package me.gr.topeka.quiz.transition

import android.annotation.TargetApi
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.app.SharedElementCallback
import androidx.core.view.updatePaddingRelative
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
open class SharedTextCallback(
    private val initialTextSize: Float,
    private val initialPaddingStart: Int
) : SharedElementCallback(), AnkoLogger {
    private var targetTextSize = 0f
    private var targetPaddingStart = 0

    override fun onSharedElementStart(
        sharedElementNames: MutableList<String>?,
        sharedElements: MutableList<View>?,
        sharedElementSnapshots: MutableList<View>?
    ) {
        val targetView = sharedElements?.first { it is TextView } as TextView?
        if (targetView == null) {
            warn("onSharedElementStart: No shared TextView, skipping.")
            return
        }
        targetTextSize = targetView.textSize
        targetPaddingStart = targetView.paddingStart
        targetView.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialTextSize)
        targetView.updatePaddingRelative(start = initialPaddingStart)
    }

    override fun onSharedElementEnd(
        sharedElementNames: MutableList<String>?,
        sharedElements: MutableList<View>?,
        sharedElementSnapshots: MutableList<View>?
    ) {
        val initialView = sharedElements?.first { it is TextView } as TextView?
        if (initialView == null) {
            warn("onSharedElementEnd: No shared TextView, skipping.")
            return
        }
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        with(initialView) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize)
            updatePaddingRelative(start = targetPaddingStart)
            measure(spec, spec)
            requestLayout()
        }
    }
}