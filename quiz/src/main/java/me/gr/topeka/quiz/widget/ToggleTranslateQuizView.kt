package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.ToggleTranslateQuiz
import me.gr.topeka.base.helper.AnswerHelper
import me.gr.topeka.quiz.R
import me.gr.topeka.quiz.adapter.OptionQuizAdapter

class ToggleTranslateQuizView(
    context: Context,
    category: Category,
    quiz: ToggleTranslateQuiz
) : AbsQuizView<ToggleTranslateQuiz>(context, category, quiz) {
    private lateinit var listView: ListView
    private var answers = BooleanArray(quiz.options.size)

    override val isCorrect: Boolean
        get() = AnswerHelper.isCorrect(
            listView.checkedItemPositions,
            quiz.answer
        )
    override var userInput: Bundle
        get() = Bundle().apply { putBooleanArray(ANSWER, answers) }
        set(value) {
            answers = value.getBooleanArray(ANSWER) ?: return
            repeat(answers.size) {
                listView.performItemClick(listView.getChildAt(it), it, listView.adapter.getItemId(it))
            }
        }

    override fun onCreateView(): View {
        listView = ListView(context).apply {
            divider = null
            choiceMode = ListView.CHOICE_MODE_MULTIPLE
            adapter = OptionQuizAdapter(
                context,
                quiz.readableOptions,
                android.R.layout.simple_list_item_multiple_choice
            )
            setSelector(R.drawable.selector_button)
            setOnItemClickListener { _, _, position, _ ->
                answers[position] = !answers[position]
                allowAnswer()
            }
        }
        return listView
    }
}