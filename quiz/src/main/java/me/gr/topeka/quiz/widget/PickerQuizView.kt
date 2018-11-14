package me.gr.topeka.quiz.widget

import android.content.Context
import android.os.Bundle
import android.view.View
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.PickerQuiz

class PickerQuizView(
    context: Context,
    category: Category,
    quiz:PickerQuiz
) :AbsQuizView<PickerQuiz>(context, category, quiz){
    override val isCorrect: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override var userInput: Bundle
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun onCreateView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}