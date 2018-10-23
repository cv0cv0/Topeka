package me.gr.topeka.base.data

class AlphaPickerQuiz(question: String, answer: String, solved: Boolean) :
    Quiz<String>(question, answer, solved) {

    override val type = QuizType.ALPHA_PICKER
}