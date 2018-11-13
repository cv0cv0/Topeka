package me.gr.topeka.base.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun <T : View> ViewGroup.inflate(
    inflater: LayoutInflater = LayoutInflater.from(context),
    @LayoutRes resource: Int,
    attachToRoot: Boolean = false
) = inflater.inflate(resource, this, attachToRoot) as T
