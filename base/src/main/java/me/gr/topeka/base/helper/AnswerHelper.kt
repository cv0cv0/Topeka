package me.gr.topeka.base.helper

import android.util.SparseBooleanArray

object AnswerHelper {
    private val separator = System.getProperty("line.separator")

    fun getAnswer(answers: IntArray, options: Array<String>): String {
        return getReadableAnswer(Array(answers.size) { options[answers[it]] })
    }

    fun getReadableAnswer(answers: Array<String>): String {
        val readableAnswer = StringBuilder()
        repeat(answers.size) {
            readableAnswer.append(answers[it])
            if (it < answers.size - 1) {
                readableAnswer.append(separator)
            }
        }
        return readableAnswer.toString()
    }

    fun isCorrect(checkedItems: SparseBooleanArray, answerIds: IntArray): Boolean {
        var checkedCount = 0
        repeat(answerIds.size) {
            if (!checkedItems.get(answerIds[it])) return false
            checkedCount++
        }
        return checkedCount == answerIds.size
    }
}