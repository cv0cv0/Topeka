package me.gr.topeka.quiz.widget

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateMarginsRelative
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import kotlinx.android.synthetic.main.view_question.view.*
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.Quiz
import me.gr.topeka.base.extension.FOREGROUND_COLOR
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.quiz.QuizActivity
import me.gr.topeka.quiz.R
import me.gr.topeka.base.R as R_base

abstract class AbsQuizView<out Q : Quiz<*>>(
    context: Context,
    private val category: Category,
    val quiz: Q
) : FrameLayout(context) {
    protected val inflater: LayoutInflater = LayoutInflater.from(context)
    private val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val doubleSpacing = resources.getDimensionPixelSize(R_base.dimen.spacing_double)
    private val submitButton = inflate<FloatingCheckableButton>(inflater, R.layout.answer_submit).apply {
        setOnClickListener {
            submitAnswer()
            isEnabled = false
        }
        hide()
    }
    private val contentView = onCreateView()
    private var isAnswered = false

    protected abstract val isCorrect: Boolean
    abstract var userInput: Bundle

    companion object {
        const val ANSWER = "answer"
        private const val ANSWER_HIDE_DELAY = 500L
        private const val FOREGROUND_COLOR_CHANGE_DELAY = 750L

        private val handler = Handler()
        private val linearOutSlowInInterpolator = LinearOutSlowInInterpolator()
    }

    init {
        id = quiz.id
        with(contentView) {
            id = R_base.id.quiz_content
            if (this is ViewGroup) {
                clipToPadding = false
            }
            minimumHeight = resources.getDimensionPixelSize(R_base.dimen.min_height_question)
        }
        buildView()
        doOnNextLayout { addFloatingButton() }
    }

    protected abstract fun onCreateView(): View

    protected open fun submitAnswer() {
        quiz.solved = true
        if (imm.isAcceptingText) {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        val backgroundColor = ContextCompat.getColor(
            context,
            if (isCorrect) R_base.color.green else R_base.color.red
        )
        adjustFab(backgroundColor)
        resizeView()
        moveViewOffScreen()
        animateForegroundColor(backgroundColor)
    }

    protected fun allowAnswer(isAnswered: Boolean = true) {
        this.isAnswered = isAnswered
        with(submitButton) {
            if (isAnswered) {
                show()
            } else {
                hide()
            }
        }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }

    private fun buildView() {
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val container = LinearLayout(context).apply {
            id = R_base.id.absQuizViewContainer
            orientation = LinearLayout.VERTICAL
        }
        val textView = container.inflate<TextView>(inflater, R.layout.view_question)
        with(textView) {
            text = quiz.question
            setBackgroundColor(ContextCompat.getColor(context, category.theme.primaryColor))
        }
        with(container) {
            addView(textView, params)
            addView(contentView, params)
        }
        addView(container, params)
    }

    private fun addFloatingButton() {
        val size = resources.getDimensionPixelSize(R_base.dimen.size_fab)
        val params = FrameLayout.LayoutParams(size, size, Gravity.TOP or Gravity.END).apply {
            updateMarginsRelative(
                top = question_text.bottom - size / 2,
                end = doubleSpacing,
                bottom = doubleSpacing
            )
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                topMargin -= submitButton.paddingTop / 2
            }
        }
        addView(submitButton, params)
    }

    private fun adjustFab(backgroundColor: Int) {
        submitButton.isChecked = isCorrect
        submitButton.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        handler.postDelayed(ANSWER_HIDE_DELAY) { submitButton.hide() }
    }

    private fun resizeView() {
        val ratio = height / width.toFloat()
        ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 0.5f).run {
            interpolator = linearOutSlowInInterpolator
            startDelay = FOREGROUND_COLOR_CHANGE_DELAY + 200
            start()
        }
        ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 0.5f / ratio).run {
            interpolator = linearOutSlowInInterpolator
            startDelay = FOREGROUND_COLOR_CHANGE_DELAY + 300
            start()
        }
    }

    private fun moveViewOffScreen() {
        category.setScore(quiz, isCorrect)
        handler.postDelayed(FOREGROUND_COLOR_CHANGE_DELAY * 2) {
            (context as QuizActivity).submitAnswer()
        }
    }

    private fun animateForegroundColor(@ColorInt targetColor: Int) {
        ObjectAnimator.ofInt(this, FOREGROUND_COLOR, Color.TRANSPARENT, targetColor).run {
            setEvaluator(ArgbEvaluator())
            startDelay = FOREGROUND_COLOR_CHANGE_DELAY
            start()
        }
    }
}