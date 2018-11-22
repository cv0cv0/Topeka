package me.gr.topeka.base.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.ui.SignInFragment

private const val URL_BASE = "https://topeka.gr.me"
private const val URL_SIGN_IN = "$URL_BASE/sign_in"
private const val URL_CATEGORIES = "$URL_BASE/categories"
private const val URL_QUIZ_BASE = "$URL_BASE/quiz"

fun Context.quizIntent(category: Category): Intent = baseIntent("$URL_QUIZ_BASE/${category.id}")

fun Activity.launchSignIn(isEditMode: Boolean) = ActivityCompat.startActivity(
    this,
    signInIntent(isEditMode),
    ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
)

fun Activity.launchCategory(options: ActivityOptionsCompat? = null) {
    if (options == null) {
        startActivity(categoryIntent)
    } else {
        ActivityCompat.startActivity(this, categoryIntent, options.toBundle())
    }
}

private val Context.categoryIntent get() = baseIntent(URL_CATEGORIES)

private fun Context.signInIntent(isEditMode: Boolean = false) = baseIntent(URL_SIGN_IN)
    .putExtra(SignInFragment.EDIT_MODE, isEditMode)

private fun Context.baseIntent(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    .addCategory(Intent.CATEGORY_DEFAULT)
    .addCategory(Intent.CATEGORY_BROWSABLE)
    .also { it.`package` = packageName }