package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.core.view.doOnNextLayout
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.FourQuarterQuiz
import me.gr.topeka.quiz.R
import me.gr.topeka.quiz.adapter.OptionQuizAdapter

class FourQuarterQuizView(
    context: Context,
    category: Category,
    quiz: FourQuarterQuiz
) : AbsQuizView<FourQuarterQuiz>(context, category, quiz) {
    private lateinit var gridView: GridView
    private var selectedPosition = -1

    override val isCorrect: Boolean
        get() = quiz.isCorrect(intArrayOf(selectedPosition))
    override var userInput: Bundle
        get() = Bundle().apply { putInt(ANSWER, selectedPosition) }
        set(value) {
            selectedPosition = value.getInt(ANSWER)
            if (selectedPosition != -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isLaidOut) {
                    setUpUserInput()
                }else{
                    doOnNextLayout { setUpUserInput() }
                }
            }
        }

    override fun onCreateView(): View {
        gridView = GridView(context).apply {
            numColumns = 2
            adapter = OptionQuizAdapter(context, quiz.options, R.layout.item_answer_center)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedPosition = position
                allowAnswer()
            }
            setSelector(me.gr.topeka.base.R.drawable.selector_button)
        }
        return gridView
    }

    private fun setUpUserInput() = with(gridView) {
        performItemClick(
            getChildAt(selectedPosition),
            selectedPosition,
            selectedPosition.toLong()
        )
        getChildAt(selectedPosition).isSelected = true
        setSelection(selectedPosition)
    }
}