package me.gr.topeka.quiz.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import me.gr.topeka.base.data.Category

class QuizAdapter(
    private val context: Context,
    private val category: Category
) : BaseAdapter() {
    private val quizzes = category.quizzes
    private val quizTypes = List(quizzes.size) { quizzes[it].type.jsonName }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(position: Int): Any = quizzes[position]

    override fun getItemId(position: Int): Long = quizzes[position].id.toLong()

    override fun getCount(): Int = quizzes.size

    override fun getItemViewType(position: Int): Int = quizTypes.indexOf(quizzes[position].type.jsonName)

    override fun getViewTypeCount(): Int = quizTypes.size

    override fun hasStableIds(): Boolean = true
}