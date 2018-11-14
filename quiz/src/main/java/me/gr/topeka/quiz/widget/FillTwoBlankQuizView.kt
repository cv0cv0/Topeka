package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.FillTwoBlankQuiz

class FillTwoBlankQuizView(
    context: Context,
    category: Category,
    quiz: FillTwoBlankQuiz
) : TextInputQuizView<FillTwoBlankQuiz>(context, category, quiz) {
    private lateinit var oneEditText: EditText
    private lateinit var twoEditText: EditText

    companion object {
        private const val ANSWER_ONE = "ANSWER_ONE"
        private const val ANSWER_TWO = "ANSWER_TWO"
    }

    override val isCorrect: Boolean
        get() = quiz.isCorrect(
            arrayOf(
                oneEditText.text.toString(),
                twoEditText.text.toString()
            )
        )
    override var userInput: Bundle
        get() = Bundle().apply {
            putString(ANSWER_ONE,oneEditText.text.toString())
            putString(ANSWER_TWO,twoEditText.text.toString())
        }
        set(value) {
            oneEditText.setText(value.getString(ANSWER_ONE))
            twoEditText.setText(value.getString(ANSWER_TWO))
        }

    override fun onCreateView(): View {
        oneEditText = onCreateEditText().apply {
            imeOptions = EditorInfo.IME_ACTION_NEXT
        }
        twoEditText = onCreateEditText().apply {
            id = me.gr.topeka.base.R.id.quiz_edit_text_two
        }
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F)
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(oneEditText, params)
            addView(twoEditText, params)
        }
    }
}