package me.gr.topeka.base.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import me.gr.topeka.base.R

class AvatarView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr), Checkable {
    private var isChecked = false

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.AvatarView, defStyleAttr, 0)
        val resId = attributes.getResourceId(R.styleable.AvatarView_avatar, 0)
        attributes.recycle()
        setAvatar(resId)
    }

    fun setAvatar(resId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToOutline = true
            setImageResource(resId)
        } else {
            val drawable =
                ResourcesCompat.getDrawable(resources, resId, context.theme) as? BitmapDrawable
            val roundedDrawable = RoundedBitmapDrawableFactory.create(resources, drawable?.bitmap)
                .apply { isCircular = true }
            setImageDrawable(roundedDrawable)
        }
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    override fun setChecked(checked: Boolean) {
        isChecked = checked
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isChecked) {
            ContextCompat.getDrawable(context, R.drawable.selector_avatar)?.apply {
                setBounds(0, 0, width, height)
                draw(canvas)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (w > 0 && h > 0) {
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val size = Math.min(w, h)
                        outline.setOval(0, 0, size, size)
                    }
                }
            }
        }
    }
}