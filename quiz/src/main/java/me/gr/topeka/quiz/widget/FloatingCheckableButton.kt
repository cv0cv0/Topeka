package me.gr.topeka.quiz.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.gr.topeka.quiz.R

class FloatingCheckableButton(context: Context, attrs: AttributeSet) :
    FloatingActionButton(context, attrs), Checkable {
    private var checked = true
    private val attrs = intArrayOf(android.R.attr.state_checked)

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (checked) {
            View.mergeDrawableStates(drawableState, attrs)
        }
        setImageResource(R.drawable.answer_quiz_fab)
        return drawableState
    }

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        checked = !checked
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked==checked){
            return
        }
        this.checked=checked
        refreshDrawableState()
    }
}