package me.gr.topeka.base.extension

import android.content.Context
import me.gr.topeka.base.helper.TopekaDatabaseHelper

val Context.db get() = TopekaDatabaseHelper.getInstance(this)