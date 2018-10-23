package me.gr.topeka.base.data

class TrueFalseQuiz(question: String, answer: Boolean, solved: Boolean) :
    Quiz<Boolean>(question, answer, solved) {

    override val type = QuizType.TRUE_FALSE
}