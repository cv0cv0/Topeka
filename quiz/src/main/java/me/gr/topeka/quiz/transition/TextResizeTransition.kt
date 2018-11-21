package me.gr.topeka.quiz.transition

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import me.gr.topeka.base.extension.PADDING_START
import me.gr.topeka.base.extension.TEXT_SIZE

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class TextResizeTransition(context: Context, attrs: AttributeSet) : Transition(context, attrs) {
    companion object {
        private const val TEXT_SIZE = "transition:textSize"
        private const val PADDING_START = "transition:paddingStart"
        private val TRANSITION_PROPERTIES = arrayOf(
            TEXT_SIZE,
            PADDING_START
        )
    }

    override fun captureStartValues(transitionValues: TransitionValues) =
        captureValues(transitionValues)

    override fun captureEndValues(transitionValues: TransitionValues) =
        captureValues(transitionValues)

    override fun getTransitionProperties(): Array<String> =
        TRANSITION_PROPERTIES

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val initialTextSize = startValues.values[TEXT_SIZE] as Float
        val targetTextSize = endValues.values[TEXT_SIZE] as Float
        val initialPaddingStart = startValues.values[PADDING_START] as Int
        val targetPaddingStart = endValues.values[PADDING_START] as Int
        val targetView = (endValues.view as TextView).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, initialTextSize)
        }
        return AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    targetView,
                    targetView.TEXT_SIZE,
                    initialTextSize,
                    targetTextSize
                ),
                ObjectAnimator.ofInt(
                    targetView,
                    targetView.PADDING_START,
                    initialPaddingStart,
                    targetPaddingStart
                )
            )
        }
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view !is TextView) {
            throw UnsupportedOperationException("Doesn't work on ${transitionValues.view.javaClass.name}")
        }
        val textView = transitionValues.view as TextView
        with(transitionValues.values) {
            put(TEXT_SIZE, textView.textSize)
            put(PADDING_START, textView.paddingStart)
        }
    }
}