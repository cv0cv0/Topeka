package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.TrueFalseQuiz
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.quiz.R

class TrueFalseQuizView(
    context: Context,
    category: Category,
    quiz: TrueFalseQuiz
) : AbsQuizView<TrueFalseQuiz>(context, category, quiz) {
    private lateinit var trueItem: View
    private lateinit var falseItem: View
    private var answer = false

    override val isCorrect: Boolean
        get() = quiz.isCorrect(answer)
    override var userInput: Bundle
        get() = Bundle().apply { putBoolean(ANSWER, answer) }
        set(value) {
            if (value.getBoolean(ANSWER)) {
                trueItem.performClick()
            } else {
                falseItem.performClick()
            }
        }

    override fun onCreateView(): View {
        val view = inflate<View>(inflater, R.layout.quiz_true_or_false)
        trueItem = view.findViewById(R.id.true_radio)
        trueItem.setOnClickListener {
            answer = true
            allowAnswer()
        }
        falseItem = view.findViewById(R.id.false_radio)
        falseItem.setOnClickListener {
            answer = false
            allowAnswer()
        }
        return view
    }
}