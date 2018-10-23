package me.gr.topeka.base.data

import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import me.gr.topeka.base.R

enum class Theme(
    @ColorRes val primaryColor: Int,
    @ColorRes val primaryDarkColor: Int,
    @ColorRes val windowBackgroundColor: Int,
    @ColorRes val textPrimaryColor: Int,
    @ColorRes val accentColor: Int,
    @StyleRes val style: Int
) {
    TOPEKA(
        R.color.topeka_primary, R.color.topeka_primary_dark,
        R.color.theme_blue_background, R.color.theme_blue_text,
        R.color.topeka_accent, R.style.Topeka
    ),

    BLUE(
        R.color.theme_blue_primary, R.color.theme_blue_primary_dark,
        R.color.theme_blue_background, R.color.theme_blue_text,
        R.color.theme_blue_accent, R.style.Topeka_Blue
    ),

    GREEN(
        R.color.theme_green_primary, R.color.theme_green_primary_dark,
        R.color.theme_green_background, R.color.theme_green_text,
        R.color.theme_green_accent, R.style.Topeka_Green
    ),

    PURPLE(
        R.color.theme_purple_primary, R.color.theme_purple_primary_dark,
        R.color.theme_purple_background, R.color.theme_purple_text,
        R.color.theme_purple_accent, R.style.Topeka_Purple
    ),

    RED(
        R.color.theme_red_primary, R.color.theme_red_primary_dark,
        R.color.theme_red_background, R.color.theme_red_text,
        R.color.theme_red_accent, R.style.Topeka_Red
    ),

    YELLOW(
        R.color.theme_yellow_primary, R.color.theme_yellow_primary_dark,
        R.color.theme_yellow_background, R.color.theme_yellow_text,
        R.color.theme_yellow_accent, R.style.Topeka_Yellow
    )
}