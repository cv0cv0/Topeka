package me.gr.topeka.base.ext

import android.database.sqlite.SQLiteDatabase

inline fun SQLiteDatabase.transaction(transaction: SQLiteDatabase.() -> Unit) {
    try {
        beginTransaction()
        transaction()
        setTransactionSuccessful()
    } finally {
        endTransaction()
    }
}