package me.gr.topeka.base.data

enum class QuizType(val jsonName: String, val type: Class<out Quiz<*>>) {
    ALPHA_PICKER("alpha-picker", AlphaPickerQuiz::class.java),

    FILL_BLANK("fill-blank", FillBlankQuiz::class.java),

    FILL_TWO_BLANKS("fill-two-blanks", FillTwoBlankQuiz::class.java),

    FOUR_QUARTER("four-quarter", FourQuarterQuiz::class.java),

    MULTI_SELECT("multi-select", MultiSelectQuiz::class.java),

    PICKER("picker", PickerQuiz::class.java),

    SINGLE_SELECT("single-select", SelectItemQuiz::class.java),

    SINGLE_SELECT_ITEM("single-select-item", SelectItemQuiz::class.java),

    TOGGLE_TRANSLATE("toggle-translate", ToggleTranslateQuiz::class.java),

    TRUE_FALSE("true-false", TrueFalseQuiz::class.java)
}