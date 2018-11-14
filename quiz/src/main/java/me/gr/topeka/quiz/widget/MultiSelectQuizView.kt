package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.MultiSelectQuiz
import me.gr.topeka.base.helper.AnswerHelper
import me.gr.topeka.quiz.adapter.OptionQuizAdapter

class MultiSelectQuizView(
    context: Context,
    category: Category,
    quiz: MultiSelectQuiz
) : AbsQuizView<MultiSelectQuiz>(context, category, quiz) {
    private lateinit var listView: ListView

    override val isCorrect: Boolean
        get() = AnswerHelper.isCorrect(listView.checkedItemPositions, quiz.answer)
    override var userInput: Bundle
        get() = Bundle().apply { putBooleanArray(ANSWER, getAnswers()) }
        set(value) {
            val answers = value.getBooleanArray(ANSWER) ?: return
            answers.indices.forEach { listView.setItemChecked(it, answers[it]) }
        }

    override fun onCreateView(): View {
        listView = ListView(context).apply {
            adapter = OptionQuizAdapter(context, quiz.options, android.R.layout.simple_list_item_multiple_choice)
            choiceMode = ListView.CHOICE_MODE_MULTIPLE
            itemsCanFocus = false
            onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> allowAnswer() }
        }
        return listView
    }

    private fun getAnswers(): BooleanArray {
        val checkedItemPositions = listView.checkedItemPositions
        return BooleanArray(checkedItemPositions.size()) { checkedItemPositions.valueAt(it) }
    }
}