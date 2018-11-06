package me.gr.topeka.categories.ui

import android.app.Activity
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.helper.TransitionHelper
import me.gr.topeka.base.helper.quizIntent
import me.gr.topeka.categories.adapter.CategoryAdapter

class CategoryFragment : Fragment() {
    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        activity?.let {
            CategoryAdapter(it) { view, category ->
                startQuizWithTransition(it, view, category)
            }
        }
    }

    companion object {
        internal const val REQUEST_CODE = 0x2300
    }

    private fun startQuizWithTransition(activity: Activity, view: View, category: Category) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            *TransitionHelper.createSafeTransitionParticipants(
                activity,
                false,
                Pair(view, activity.getString(me.gr.topeka.base.R.string.transition_toolbar))
            )
        ).toBundle()
        ActivityCompat.startActivityForResult(
            activity,
            activity.quizIntent(category),
            REQUEST_CODE,
            options
        )
    }
}