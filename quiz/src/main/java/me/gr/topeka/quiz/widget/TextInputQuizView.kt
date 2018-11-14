package me.gr.topeka.quiz.widget

import android.content.Context
import android.text.Editable
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.Quiz
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.base.text.TextWatcherAdapter
import me.gr.topeka.quiz.R

abstract class TextInputQuizView<out Q : Quiz<*>>(
    context: Context,
    category: Category,
    quiz: Q
) : AbsQuizView<Q>(context, category, quiz), TextView.OnEditorActionListener {

    protected fun onCreateEditText(): EditText {
        val editText = inflate<EditText>(inflater, R.layout.quiz_edit)
        editText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                allowAnswer(s.isNotEmpty())
            }
        })
        editText.setOnEditorActionListener(this)
        return editText
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (v.text.isNotEmpty()) {
            allowAnswer()
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitAnswer()
                return true
            }
        }
        return false
    }
}