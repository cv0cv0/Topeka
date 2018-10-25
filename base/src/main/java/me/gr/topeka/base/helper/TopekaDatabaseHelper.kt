package me.gr.topeka.base.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import me.gr.topeka.base.R
import me.gr.topeka.base.data.*
import me.gr.topeka.base.ext.toIntArray
import me.gr.topeka.base.ext.transaction
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class TopekaDatabaseHelper private constructor(
    private val context: Context
) : SQLiteOpenHelper(context.applicationContext, "topeka.db", null, 1) {
    private val categories: MutableList<Category> = loadCategories()

    override fun onCreate(db: SQLiteDatabase) {
        with(db) {
            execSQL(CategoryTable.CREATE)
            execSQL(QuizTable.CREATE)
            fillCategoriesAndQuizzes(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

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
        MutableList(cursor.count) { i ->
            cursor.moveToPosition(i)
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

    private fun getQuizzes(id: String): List<Quiz<*>> {

    }

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