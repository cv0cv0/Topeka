package me.gr.topeka.base.data

class FillBlankQuiz(
    question: String,
    answer: String,
    val start: String?,
    val end: String?,
    solved: Boolean
) : Quiz<String>(question, answer, solved) {

    override val type = QuizType.FILL_BLANK

    override fun isCorrect(answer: String?): Boolean {
        return this.answer.equals(answer, true)
    }
}