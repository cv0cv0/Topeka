package me.gr.topeka.base.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import me.gr.topeka.base.data.CategoryTable
import me.gr.topeka.base.data.QuizTable

class TopekaDatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "topeka.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
         with(db){
             execSQL(CategoryTable.CREATE)
             execSQL(QuizTable.CREATE)
         }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}