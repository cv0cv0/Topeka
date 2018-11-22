package me.gr.topeka.base.data

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import java.util.*

data class Category(
    val id: String,
    val name: String,
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
        const val ID = "category_id"
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

    private fun getScore(which: Quiz<*>) = try {
        scores[quizzes.indexOf(which)]
    } catch (e: IndexOutOfBoundsException) {
        0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (name != other.name) return false
        if (id != other.id) return false
        if (theme != other.theme) return false
        if (quizzes != other.quizzes) return false
        if (!Arrays.equals(scores, other.scores)) return false
        if (solved != other.solved) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + theme.hashCode()
        result = 31 * result + quizzes.hashCode()
        result = 31 * result + Arrays.hashCode(scores)
        result = 31 * result + solved.hashCode()
        return result
    }
}