package me.gr.topeka.quiz.widget

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import kotlinx.android.synthetic.main.view_question.view.*
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.Quiz
import me.gr.topeka.base.extension.inflate
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
    private val answerSubmit = inflate<FloatingCheckableButton>(inflater, R.layout.answer_submit).apply {
        setOnClickListener {
            submitAnswer()
            isEnabled = false
        }
        hide()
    }

    private var isAnswered = false

    protected val contentView = onCreateView()
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
        // TODO
    }

    protected fun allowAnswer(isAnswered: Boolean = true) {
        this.isAnswered = isAnswered
        with(answerSubmit) {
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
        val textView = inflater.inflate(R.layout.quiz_edit, container, false) as TextView
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
            marginEnd = doubleSpacing
            bottomMargin = doubleSpacing
            topMargin = question_text.bottom - size / 2
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                topMargin -= answerSubmit.paddingTop / 2
            }
        }
        addView(answerSubmit, params)
    }
}