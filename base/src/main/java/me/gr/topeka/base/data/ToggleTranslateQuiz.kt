package me.gr.topeka.base.data

import me.gr.topeka.base.helper.AnswerHelper

class ToggleTranslateQuiz(
    question: String,
    answer: IntArray,
    options: Array<Array<String>>,
    solved: Boolean
) : OptionsQuiz<Array<String>>(question, answer, options, solved) {

    override val type = QuizType.TOGGLE_TRANSLATE

    val readableOptions = Array(options.size) { "${options[it][0]} <> ${options[it][1]}" }

    fun IntArray.toString() = AnswerHelper.getAnswer(this, readableOptions)
}