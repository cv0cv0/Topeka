package me.gr.topeka.base.data

class PickerQuiz(question: String, answer: Int, solved: Boolean) :
    Quiz<Int>(question, answer, solved) {

    override val type = QuizType.PICKER
}