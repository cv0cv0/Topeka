package me.gr.topeka.quiz.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.Quiz
import me.gr.topeka.base.R as R_base

abstract class AbsQuizView<out Q : Quiz<*>>(
    context: Context,
    private val category: Category,
    val quiz: Q
) : FrameLayout(context) {
    private val inflater = LayoutInflater.from(context)
    protected val contentView = onCreateView()

    companion object {
        const val ANSWER = "answer"
    }

    protected abstract fun onCreateView(): View

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
    }

    private fun buildView() {
        val container = LinearLayout(context).apply {
            id = R_base.id.absQuizViewContainer
            orientation = LinearLayout.VERTICAL
        }
    }
}