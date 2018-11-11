package me.gr.topeka.quiz.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import me.gr.topeka.base.data.Category
import me.gr.topeka.quiz.R
import me.gr.topeka.base.R as R_base

class ScoreAdapter(
    private val context: Context,
    private val category: Category
) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val quizzes = category.quizzes
    private val quizCount = quizzes.size
    private val correctDrawable by lazy(LazyThreadSafetyMode.NONE) {
        createTintDrawable(
            R_base.drawable.ic_tick,
            R_base.color.theme_green_primary
        )
    }
    private val wrongDrawable by lazy(LazyThreadSafetyMode.NONE) {
        createTintDrawable(
            R_base.drawable.ic_cross,
            R_base.color.theme_red_primary
        )
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        if (convertView == null) {
            val itemView = inflater.inflate(R.layout.item_score, parent, false)
            holder = ViewHolder(itemView)
            itemView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val quiz = quizzes[position]
        with(holder) {
            solvedImage.setImageDrawable(
                if (category.isSolvedCorrectly(quiz)) correctDrawable else wrongDrawable
            )
            quizText.text = quiz.question
            answerText.text = quiz.answer.toString()
        }
        return holder.itemView
    }

    override fun getItem(position: Int): Any = quizzes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = quizCount

    private fun createTintDrawable(
        @DrawableRes drawableRes: Int,
        @ColorRes tintColorRes: Int
    ): Drawable = ContextCompat.getDrawable(context, drawableRes)!!
        .let { DrawableCompat.wrap(it) }
        .also { DrawableCompat.setTint(it, ContextCompat.getColor(context, tintColorRes)) }

    private class ViewHolder(val itemView: View) {
        val solvedImage: ImageView = itemView.findViewById(R.id.solved_image)
        val answerText: TextView = itemView.findViewById(R.id.answer_text)
        val quizText: TextView = itemView.findViewById(R.id.answer_text)
    }
}