package me.gr.topeka.base.ext

import android.content.Context
import me.gr.topeka.base.helper.TopekaDatabaseHelper

val Context.db
    get() = TopekaDatabaseHelper.getInstance(this)