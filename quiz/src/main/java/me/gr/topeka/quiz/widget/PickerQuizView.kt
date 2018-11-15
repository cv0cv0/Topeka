package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.PickerQuiz
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.quiz.R

class PickerQuizView(
    context: Context,
    category: Category,
    quiz: PickerQuiz
) : AbsQuizView<PickerQuiz>(context, category, quiz) {
    private lateinit var selectionText: TextView
    private lateinit var seekBar: SeekBar
    private var progress = 0

    override val isCorrect: Boolean
        get() = quiz.isCorrect(progress)
    override var userInput: Bundle
        get() = Bundle().apply { putInt(ANSWER, progress) }
        set(value) {
            seekBar.progress = value.getInt(ANSWER) - quiz.min
        }

    override fun onCreateView(): View {
        val view = inflate<View>(inflater, R.layout.quiz_picker)
        selectionText = view.findViewById(R.id.pick_text)
        selectionText.text = quiz.min.toString()
        seekBar = findViewById(R.id.seek_bar)
        seekBar.max = quiz.max - quiz.min
        val step = if (quiz.step == 0) 1 else quiz.step
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                this@PickerQuizView.progress = (quiz.min + progress) / step * step
                selectionText.text = this@PickerQuizView.progress.toString()
                allowAnswer()
            }
        })
        return view
    }
}