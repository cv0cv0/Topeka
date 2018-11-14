package me.gr.topeka.base.data

import me.gr.topeka.base.helper.AnswerHelper

class FillTwoBlankQuiz(question: String, answer: Array<String>, solved: Boolean) :
    Quiz<Array<String>>(question, answer, solved) {

    override val type = QuizType.FILL_TWO_BLANKS

    fun Array<String>.toString() = AnswerHelper.getReadableAnswer(this)
}