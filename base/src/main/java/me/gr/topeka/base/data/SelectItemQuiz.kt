package me.gr.topeka.base.data

import me.gr.topeka.base.helper.AnswerHelper

class SelectItemQuiz(question: String, answer: IntArray, options: Array<String>, solved: Boolean) :
    OptionsQuiz<String>(question, answer, options, solved) {

    override val type = QuizType.SINGLE_SELECT

    fun IntArray.toString() = AnswerHelper.getAnswer(this, options)
}