package me.gr.topeka.base.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Property
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.updatePaddingRelative

val View.PROPERTY_PADDING_START
    get() = object : IntProperty<View>("paddingStart") {
        override fun get(`object`: View?): Int = paddingStart

        override fun set(`object`: View?, value: Int) = updatePaddingRelative(start = value)
    }

val View.PROPERTY_BACKGROUND_COLOR
    get() = object : IntProperty<View>("backgroundColor") {
        override fun get(`object`: View?): Int =
            (background as? ColorDrawable)?.color ?: Color.TRANSPARENT

        override fun set(`object`: View?, value: Int) = setBackgroundColor(value)
    }

val FrameLayout.PROPERTY_FOREGROUND_COLOR
    get() = object : IntProperty<FrameLayout>("foregroundColor") {
        override fun get(`object`: FrameLayout?): Int =
            (foreground as? ColorDrawable)?.color ?: Color.TRANSPARENT

        override fun set(`object`: FrameLayout?, value: Int) = if (foreground is ColorDrawable) {
            (foreground as ColorDrawable).color = value
        } else {
            foreground = ColorDrawable(value)
        }
    }

val TextView.PROPERTY_TEXT_SIZE
    get() = object : FloatProperty<TextView>("textSize") {
        override fun get(`object`: TextView?): Float = textSize

        override fun set(`object`: TextView?, value: Float) =
            setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
    }

abstract class IntProperty<T>(name: String) : Property<T, Int>(Int::class.java, name)

abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name)