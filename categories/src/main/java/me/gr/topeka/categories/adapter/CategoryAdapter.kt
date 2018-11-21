package me.gr.topeka.categories.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.instantapps.InstantApps
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.extension.db
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.categories.R

class CategoryAdapter(
    private val activity: Activity,
    private val onItemClickListener: (view: View, category: Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(activity)
    private var categories = activity.db.getCategories(true)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate<View>(inflater, R.layout.item_category))

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        with(holder.itemView) {
            setBackgroundColor(getColor(activity, category.theme.windowBackgroundColor))
            setOnClickListener { onItemClickListener.invoke(holder.title, category) }
        }
        with(holder.title) {
            text = category.name
            setTextColor(getColor(activity, category.theme.textPrimaryColor))
            setBackgroundColor(getColor(activity, category.theme.primaryColor))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                transitionName = category.name
            }
        }
        with(holder.image) {
            val packageName = activity.packageName +
                    if (InstantApps.isInstantApp(activity)) ".categories" else ""
            val resId = activity.resources.getIdentifier(
                "icon_category_${category.id}",
                "drawable",
                packageName
            )
            if (category.solved) {
                val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    LayerDrawable(
                        arrayOf(
                            getTintedDrawable(resId, category.theme.primaryColor),
                            getTintedDrawable(
                                me.gr.topeka.base.R.drawable.ic_tick,
                                android.R.color.white
                            )
                        )
                    )
                } else {
                    getTintedDrawable(resId, category.theme.primaryColor)
                }
                setImageDrawable(drawable)
            } else {
                setImageResource(resId)
            }
        }
    }

    fun notifyItemChanged(id: String) {
        categories = activity.db.getCategories(true)
        val position = categories.indices.firstOrNull { categories[it].id == id } ?: -1
        notifyItemChanged(position)
    }

    private fun getTintedDrawable(
        @DrawableRes drawableRes: Int,
        @ColorRes tintColorRes: Int
    ): Drawable = getDrawable(activity, drawableRes)!!.mutate()
        .let { DrawableCompat.wrap(it) }
        .also { DrawableCompat.setTint(it, getColor(activity, tintColorRes)) }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.category_image)
        val title: TextView = itemView.findViewById(R.id.category_title)
    }
}