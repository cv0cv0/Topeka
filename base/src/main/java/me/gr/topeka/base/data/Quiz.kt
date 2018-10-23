package me.gr.topeka.base.data

abstract class Quiz<A> internal constructor(
    val question: String,
    val answer: A,
    var solved: Boolean
) {
    val id = question.hashCode()

    abstract val type: QuizType

    open fun isCorrect(answer: A?): Boolean {
        return this.answer == answer
    }

    override fun toString(): String {
        return "$type: $question"
    }
}