package me.gr.topeka.quiz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.quiz.R

class OptionQuizAdapter(
    context: Context,
    private val options: Array<String>,
    private val layoutId: Int,
    withPrefix: Boolean = false
) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val alphabet: Array<String> = if (withPrefix) {
        context.resources.getStringArray(R.array.alphabet)
    } else {
        emptyArray()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = if (convertView != null) {
            convertView as TextView
        } else {
            parent.inflate(inflater, layoutId)
        }
        textView.text = if (alphabet.isNotEmpty()) {
            "${getPrefix(position)} ${options[position]}"
        } else {
            options[position]
        }
        return textView
    }

    override fun getItem(position: Int): Any = options[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = options.size

    override fun hasStableIds(): Boolean = true

    private fun getPrefix(position: Int): String {
        val size = alphabet.size
        if (position >= size) {
            throw IllegalArgumentException("Only position between 0 and $size are supported")
        }
        return "${alphabet[position]}."
    }
}