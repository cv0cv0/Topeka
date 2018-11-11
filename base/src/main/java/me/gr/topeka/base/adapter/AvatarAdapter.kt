package me.gr.topeka.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import me.gr.topeka.base.R
import me.gr.topeka.base.data.Avatar
import me.gr.topeka.base.widget.AvatarView

class AvatarAdapter(context: Context) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    companion object {
        private val avatars = Avatar.values()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = convertView ?: inflater.inflate(R.layout.item_avatar, parent, false)
        (itemView as AvatarView).setAvatar(avatars[position].resId)
        return itemView
    }

    override fun getItem(position: Int): Any = avatars[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = avatars.size
}