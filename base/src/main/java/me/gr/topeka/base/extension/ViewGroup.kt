package me.gr.topeka.base.extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes resource: Int) = LayoutInflater.from(context).inflate(resource, this, false)!!
