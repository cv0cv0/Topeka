package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import me.gr.topeka.base.data.AlphaPickerQuiz
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.quiz.R

class AlphaPickerQuizView(
    context: Context,
    category: Category,
    quiz: AlphaPickerQuiz
) : AbsQuizView<AlphaPickerQuiz>(context, category, quiz) {
    private val alphabet = resources.getStringArray(R.array.alphabet)
    private lateinit var selectionText: TextView
    private lateinit var seekBar: SeekBar

    companion object {
        private const val SELECTION = "SELECTION"
    }

    override var userInput: Bundle
        get() = Bundle().apply { putString(SELECTION, selectionText.text.toString()) }
        set(value) {
            seekBar.progress = alphabet.indexOf(value.getString(SELECTION, alphabet[0]))
        }
    override val isCorrect: Boolean
        get() = quiz.isCorrect(selectionText.text.toString())

    override fun onCreateView(): View {
        val view = inflate<View>(inflater, R.layout.quiz_picker)
        selectionText = view.findViewById(R.id.pick_text)
        selectionText.text = alphabet[0]
        seekBar = view.findViewById(R.id.seek_bar)
        with(seekBar) {
            max = alphabet.size - 1
            setOnSeekBarChangeListener(object : OnSeekBarChangeListenerAdapter() {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    selectionText.text = alphabet[progress]
                    allowAnswer()
                }
            })
        }
        return view
    }
}