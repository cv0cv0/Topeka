package me.gr.topeka.base.ext

import org.json.JSONArray

fun JSONArray.toIntArray() = IntArray(length()) { getInt(it) }

fun JSONArray.toStringArray() = Array(length()) { getString(it) }