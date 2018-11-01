package me.gr.topeka.base.helper

import android.util.Property
import android.view.View

val View.PROPERTY_PADDING_START
    get() = object : IntProperty<View>("paddingStart") {
        override fun get(`object`: View?): Int = paddingStart

        override fun set(`object`: View?, value: Int) = setPaddingStart(value)
    }

fun View.setPaddingStart(paddingStart: Int) =
    setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom)

abstract class IntProperty<T>(name: String) : Property<T, Int>(Int::class.java, name)

abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name)