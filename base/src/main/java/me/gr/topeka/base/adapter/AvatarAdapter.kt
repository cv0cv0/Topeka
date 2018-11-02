package me.gr.topeka.base.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import me.gr.topeka.base.R
import me.gr.topeka.base.data.Avatar
import me.gr.topeka.base.extension.inflate
import me.gr.topeka.base.widget.AvatarView

class AvatarAdapter : BaseAdapter() {
    companion object {
        private val avatars = Avatar.values()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        ((convertView ?: parent.inflate(R.layout.item_avatar)) as AvatarView)
            .apply { setAvatar(avatars[position].resId) }

    override fun getItem(position: Int): Any = avatars[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = avatars.size
}