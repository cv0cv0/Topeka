package me.gr.topeka.categories.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.JsonAttributes
import me.gr.topeka.base.helper.TransitionHelper
import me.gr.topeka.base.helper.quizIntent
import me.gr.topeka.categories.R
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view.findViewById<RecyclerView>(R.id.category_recycler)) {
            adapter = this@CategoryFragment.adapter
            val size = context.resources
                .getDimensionPixelSize(me.gr.topeka.base.R.dimen.spacing_nano)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) = outRect.set(size, size, size, size)
            })
            doOnPreDraw { activity?.supportStartPostponedEnterTransition() }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            adapter?.notifyItemChanged(data!!.getStringExtra(JsonAttributes.ID))
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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