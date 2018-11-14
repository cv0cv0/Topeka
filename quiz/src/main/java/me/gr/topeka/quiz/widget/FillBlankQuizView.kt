package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isGone
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.FillBlankQuiz
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.base.text.TextWatcherAdapter
import me.gr.topeka.quiz.R

class FillBlankQuizView(
    context: Context,
    category: Category,
    quiz: FillBlankQuiz
) : TextInputQuizView<FillBlankQuiz>(context, category, quiz) {
    private lateinit var editText: EditText

    override var userInput: Bundle
        get() = Bundle().apply { putString(ANSWER, editText.text.toString()) }
        set(value) {
            editText.setText(value.getString(ANSWER))
        }
    override val isCorrect: Boolean
        get() = quiz.isCorrect(editText.text.toString())

    override fun onCreateView(): View {
        val start = quiz.start
        val end = quiz.end
        if (start != null || end != null) {
            return onCreateSurroundingEditText(start, end)
        }
        editText = onCreateEditText()
        return editText
    }

    private fun onCreateSurroundingEditText(start: String?, end: String?): View {
        val view = inflate<View>(inflater, R.layout.quiz_edit_surrounding).apply {
            setContextOrHide(findViewById(R.id.top_text), start)
            setContextOrHide(findViewById(R.id.bottom_text), end)
        }
        editText = view.findViewById(R.id.quiz_edit)
        editText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                allowAnswer(s.isNotEmpty())
            }
        })
        editText.setOnEditorActionListener(this)
        return view
    }

    private fun setContextOrHide(textView: TextView, context: String?) {
        if (context != null) {
            textView.text = context
        } else {
            textView.isGone = true
        }
    }
}