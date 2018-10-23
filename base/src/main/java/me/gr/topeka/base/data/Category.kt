package me.gr.topeka.base.data

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

data class Categor(
    val name: String,
    val id: String,
    val theme: Theme,
    val quizzes: List<Quiz<*>>,
    val scores: IntArray,
    var solved: Boolean
) : AnkoLogger {
    init {
        if (quizzes.size != scores.size) {
            throw  IllegalArgumentException("Expected ${quizzes.size} and ${scores.size} to be of the same length")
        }
    }

    companion object {
        private const val SCORE = 8
        private const val NO_SCORE = 0
    }

    val score get() = scores.sum()

    val firstUnsolvedQuizPosition
        get() = quizzes.indices.firstOrNull { !quizzes[it].solved } ?: quizzes.size

    fun isSolvedCorrectly(quiz: Quiz<*>) = getScore(quiz) == SCORE

    fun setScore(which: Quiz<*>, correctlySolved: Boolean) {
        val index = quizzes.indexOf(which)
        debug("Setting score for $which with index $index")
        if (index == -1) return
        scores[index] = if (correctlySolved) SCORE else NO_SCORE
    }

    fun getScore(which: Quiz<*>) = try {
        scores[quizzes.indexOf(which)]
    } catch (e: IndexOutOfBoundsException) {
        0
    }
}