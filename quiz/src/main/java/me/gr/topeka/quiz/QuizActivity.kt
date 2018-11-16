package me.gr.topeka.quiz

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.helper.launchCategory
import me.gr.topeka.quiz.transition.SharedTextCallback
import me.gr.topeka.quiz.ui.QuizFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import me.gr.topeka.base.R as R_base

class QuizActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var quizFragment: QuizFragment
    private lateinit var revealAnimator: Animator
    private lateinit var colorAnimator: ObjectAnimator
    private lateinit var category: Category
    private val interpolator = FastOutSlowInInterpolator()
    private var isPlaying = false

    companion object {
        private const val CATEGORY_IMAGE_PREFIX = "image_category_"
        private const val IS_PLAYING = "is_playing"
        private const val QUIZ_FRAGMENT_TAG = "Quiz"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        if (savedInstanceState != null) {
            isPlaying = savedInstanceState.getBoolean(IS_PLAYING)
        }
        val data = intent.data
        if (data != null) {
            if (data.path!!.startsWith("/quiz")) {
                populate(data.lastPathSegment!!)
            } else {
                warn("Path is invalid, finishing activity.")
                launchCategory()
                supportFinishAfterTransition()
            }
        }
        val textSize = resources.getDimensionPixelSize(R_base.dimen.category_item_text_size).toFloat()
        val paddingStart = resources.getDimensionPixelSize(R_base.dimen.spacing_double)
        val startDelay = resources.getInteger(R_base.integer.toolbar_transition_duration).toLong()
        ActivityCompat.setEnterSharedElementCallback(this, object : SharedTextCallback(textSize, paddingStart) {
            override fun onSharedElementStart(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots)
            }

            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
            }
        })
    }

    private fun populate(categoryId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
