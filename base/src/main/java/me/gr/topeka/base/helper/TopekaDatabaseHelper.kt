package me.gr.topeka.base.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import me.gr.topeka.base.R
import me.gr.topeka.base.data.*
import me.gr.topeka.base.extension.toIntArray
import me.gr.topeka.base.extension.toStringArray
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class TopekaDatabaseHelper private constructor(
    private val context: Context
) : SQLiteOpenHelper(context.applicationContext, "topeka.db", null, 1) {
    private val categories: MutableList<Category> = loadCategories()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TopekaDatabaseHelper? = null

        fun getInstance(context: Context): TopekaDatabaseHelper =
            instance ?: synchronized(TopekaDatabaseHelper::class) {
                TopekaDatabaseHelper(context).also { instance = it }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        with(db) {
            execSQL(CategoryTable.CREATE)
            execSQL(QuizTable.CREATE)
            fillCategoriesAndQuizzes(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun getCategories(fromDatabase: Boolean = false): List<Category> {
        if (fromDatabase) {
            categories.clear()
            categories.addAll(loadCategories())
        }
        return categories
    }

    fun getCategoryById(id: String): Category = readableDatabase.query(
        CategoryTable.NAME, CategoryTable.PROJECTION,
        "${CategoryTable.COLUMN_ID}=?", arrayOf(id),
        null, null, null
    ).use { getCategory(it) }

    fun getScore() = categories.sumBy { it.score }

    fun updateCategory(category: Category) {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            with(categories) {
                removeAt(index)
                add(index, category)
            }
        }

        val values = ContentValues().apply {
            put(CategoryTable.COLUMN_SOLVED, category.solved)
            put(CategoryTable.COLUMN_SCORES, Arrays.toString(category.scores))
        }
        writableDatabase.update(
            CategoryTable.NAME, values,
            "${CategoryTable.COLUMN_ID}=?", arrayOf(category.id)
        )
        updateQuizzes(writableDatabase, category.quizzes)
    }

    fun reset() = with(writableDatabase) {
        delete(CategoryTable.NAME, null, null)
        delete(QuizTable.NAME, null, null)
        fillCategoriesAndQuizzes(this)
    }

    private fun updateQuizzes(db: SQLiteDatabase, quizzes: List<Quiz<*>>) = quizzes.forEach {
        val values = ContentValues().apply {
            put(QuizTable.COLUMN_SOLVED, it.solved)
        }
        db.update(
            QuizTable.NAME, values,
            "${QuizTable.COLUMN_ID}=?", arrayOf(it.question)
        )
    }

    private fun fillCategoriesAndQuizzes(db: SQLiteDatabase) {
        db.transaction {
            val values = ContentValues()
            val jsonArray = JSONArray(readCategoriesFromResources())
            repeat(jsonArray.length()) {
                with(jsonArray.getJSONObject(it)) {
                    val id = getString(JsonAttributes.ID)
                    fillCategory(db, id, values, this)
                    fillQuizzesForCategory(db, id, values, getJSONArray(JsonAttributes.QUIZZES))
                }
            }
        }
    }

    private fun loadCategories(): MutableList<Category> = getCategoryCursor().use { cursor ->
        MutableList(cursor.count) {
            cursor.moveToPosition(it)
            getCategory(cursor)
        }
    }

    private fun getCategoryCursor(): Cursor = readableDatabase.query(
        CategoryTable.NAME,
        CategoryTable.PROJECTION,
        null,
        null,
        null,
        null,
        null
    )

    private fun getCategory(cursor: Cursor): Category = with(cursor) {
        val id = getString(0)
        Category(
            id,
            getString(1),
            Theme.valueOf(getString(2)),
            getQuizzes(id),
            JSONArray(getString(4)).toIntArray(),
            parseSolved(getString(3))
        )
    }

    private fun getQuizzes(id: String): List<Quiz<*>> = readableDatabase.query(
        QuizTable.NAME,
        QuizTable.PROJECTION,
        "${QuizTable.FK_CATEGORY} like ? ", arrayOf(id),
        null, null, null
    ).use { cursor ->
        List(cursor.count) {
            cursor.moveToPosition(it)
            createQuizDueToType(cursor)
        }
    }

    private fun createQuizDueToType(cursor: Cursor): Quiz<*> {
        val type = cursor.getString(2)
        val question = cursor.getString(3)
        val answer = cursor.getString(4)
        val options = cursor.getString(5)
        val min = cursor.getInt(6)
        val max = cursor.getInt(7)
        val step = cursor.getInt(8)
        val solved = parseSolved(cursor.getString(11))

        return when (type) {
            QuizType.ALPHA_PICKER.jsonName ->
                AlphaPickerQuiz(question, answer, solved)
            QuizType.FILL_BLANK.jsonName ->
                FillBlankQuiz(question, answer, cursor.getString(9), cursor.getString(10), solved)
            QuizType.FILL_TWO_BLANKS.jsonName ->
                FillTwoBlanksQuiz(question, JSONArray(answer).toStringArray(), solved)
            QuizType.PICKER.jsonName ->
                PickerQuiz(question, Integer.valueOf(answer), min, max, step, solved)
            QuizType.TRUE_FALSE.jsonName ->
                TrueFalseQuiz(question, answer == "true", solved)
            QuizType.TOGGLE_TRANSLATE.jsonName ->
                createToggleTranslateQuiz(question, answer, options, solved)
            QuizType.FOUR_QUARTER.jsonName ->
                createStringOptionsQuiz(answer, options) { answerArray, optionsArray ->
                    FourQuarterQuiz(question, answerArray, optionsArray, solved)
                }
            QuizType.MULTI_SELECT.jsonName ->
                createStringOptionsQuiz(answer, options) { answerArray, optionsArray ->
                    MultiSelectQuiz(question, answerArray, optionsArray, solved)
                }
            QuizType.SINGLE_SELECT.jsonName, QuizType.SINGLE_SELECT_ITEM.jsonName ->
                createStringOptionsQuiz(answer, options) { answerArray, optionsArray ->
                    SelectItemQuiz(question, answerArray, optionsArray, solved)
                }
            else -> throw IllegalArgumentException("Quiz type $type is not supported")
        }
    }

    private fun createToggleTranslateQuiz(
        question: String,
        answer: String,
        options: String,
        solved: Boolean
    ): Quiz<*> {
        val answerArray = JSONArray(answer).toIntArray()
        val optionsArrays = with(JSONArray(options).toStringArray()) {
            Array(size) { JSONArray(this[it]).toStringArray() }
        }
        return ToggleTranslateQuiz(question, answerArray, optionsArrays, solved)
    }

    private fun <T : OptionsQuiz<String>> createStringOptionsQuiz(
        answer: String,
        options: String,
        factory: (IntArray, Array<String>) -> T
    ): T = factory(JSONArray(answer).toIntArray(), JSONArray(options).toStringArray())

    private fun parseSolved(solved: String?): Boolean {
        return solved?.length == 1 && Integer.valueOf(solved) == 1
    }

    private fun readCategoriesFromResources(): String {
        val inputStream = context.resources.openRawResource(R.raw.categories)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readText()
    }

    private fun fillCategory(
        db: SQLiteDatabase,
        id: String,
        values: ContentValues,
        jsonObject: JSONObject
    ) {
        with(values) {
            clear()
            put(CategoryTable.COLUMN_ID, id)
            put(CategoryTable.COLUMN_NAME, jsonObject.getString(JsonAttributes.NAME))
            put(CategoryTable.COLUMN_THEME, jsonObject.getString(JsonAttributes.THEME))
            put(CategoryTable.COLUMN_SOLVED, jsonObject.getString(JsonAttributes.SOLVED))
            put(CategoryTable.COLUMN_SCORES, jsonObject.getString(JsonAttributes.SCORES))
        }
        db.insert(CategoryTable.NAME, null, values)
    }

    private fun fillQuizzesForCategory(
        db: SQLiteDatabase,
        id: String,
        values: ContentValues,
        jsonArray: JSONArray
    ) {
        repeat(jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(it)
            with(values) {
                clear()
                put(QuizTable.FK_CATEGORY, id)
                put(QuizTable.COLUMN_TYPE, jsonObject.getString(JsonAttributes.TYPE))
                put(QuizTable.COLUMN_QUESTION, jsonObject.getString(JsonAttributes.QUESTION))
                put(QuizTable.COLUMN_ANSWER, jsonObject.getString(JsonAttributes.ANSWER))
            }
            putNonEmptyString(values, jsonObject, JsonAttributes.OPTIONS, QuizTable.COLUMN_OPTIONS)
            putNonEmptyString(values, jsonObject, JsonAttributes.MIN, QuizTable.COLUMN_MIN)
            putNonEmptyString(values, jsonObject, JsonAttributes.MAX, QuizTable.COLUMN_MAX)
            putNonEmptyString(values, jsonObject, JsonAttributes.START, QuizTable.COLUMN_START)
            putNonEmptyString(values, jsonObject, JsonAttributes.END, QuizTable.COLUMN_END)
            putNonEmptyString(values, jsonObject, JsonAttributes.STEP, QuizTable.COLUMN_STEP)
            db.insert(QuizTable.NAME, null, values)
        }
    }

    private fun putNonEmptyString(
        values: ContentValues,
        jsonObject: JSONObject,
        jsonKey: String,
        contentKey: String
    ) {
        jsonObject.optString(jsonKey, null)?.let { values.put(contentKey, it) }
    }
}