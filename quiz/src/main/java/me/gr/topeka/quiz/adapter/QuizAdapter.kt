package me.gr.topeka.quiz.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import me.gr.topeka.base.data.*
import me.gr.topeka.quiz.widget.*

class QuizAdapter(
    private val context: Context,
    private val category: Category
) : BaseAdapter() {
    private val quizzes = category.quizzes
    private val quizTypes = List(quizzes.size) { quizzes[it].type.jsonName }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val quiz = quizzes[position]
        return if (convertView is AbsQuizView<*> && convertView.quiz == quiz) {
            convertView
        } else {
            createViewForType(quiz)
        }
    }

    override fun getItem(position: Int): Any = quizzes[position]

    override fun getItemId(position: Int): Long = quizzes[position].id.toLong()

    override fun getCount(): Int = quizzes.size

    override fun getItemViewType(position: Int): Int = quizTypes.indexOf(quizzes[position].type.jsonName)

    override fun getViewTypeCount(): Int = quizTypes.size

    override fun hasStableIds(): Boolean = true

    private fun createViewForType(quiz: Quiz<*>): AbsQuizView<Quiz<*>> = when (quiz.type) {
        QuizType.ALPHA_PICKER -> AlphaPickerQuizView(context, category, quiz as AlphaPickerQuiz)
        QuizType.FILL_BLANK -> FillBlankQuizView(context, category, quiz as FillBlankQuiz)
        QuizType.FILL_TWO_BLANKS -> FillTwoBlankQuizView(context, category, quiz as FillTwoBlankQuiz)
        QuizType.FOUR_QUARTER -> FourQuarterQuizView(context, category, quiz as FourQuarterQuiz)
        QuizType.MULTI_SELECT -> MultiSelectQuizView(context, category, quiz as MultiSelectQuiz)
        QuizType.PICKER -> PickerQuizView(context, category, quiz as PickerQuiz)
        QuizType.SINGLE_SELECT, QuizType.SINGLE_SELECT_ITEM -> SelectItemQuizView(context, category, quiz as SelectItemQuiz)
        QuizType.TOGGLE_TRANSLATE -> ToggleTranslateQuizView(context, category, quiz as ToggleTranslateQuiz)
        QuizType.TRUE_FALSE -> TrueFalseQuizView(context, category, quiz as TrueFalseQuiz)
    }
}