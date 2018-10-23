package me.gr.topeka.base.data

import java.util.*

abstract class OptionsQuiz<T>(
    question: String,
    answer: IntArray,
    var options: Array<T>,
    solved: Boolean
) : Quiz<IntArray>(question, answer, solved) {

    override fun isCorrect(answer: IntArray?): Boolean {
        return Arrays.equals(this.answer, answer)
    }
}