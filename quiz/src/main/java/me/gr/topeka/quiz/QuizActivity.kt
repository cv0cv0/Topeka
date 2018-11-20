package me.gr.topeka.quiz

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.instantapps.InstantApps
import kotlinx.android.synthetic.main.activity_quiz.*
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.extension.db
import me.gr.topeka.base.helper.isLogged
import me.gr.topeka.base.helper.launchCategory
import me.gr.topeka.base.helper.requestLogin
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
    private var isPlaying = false

    companion object {
        private val interpolator = FastOutSlowInInterpolator()
        private const val CATEGORY_IMAGE_PREFIX = "image_category_"
        private const val IS_PLAYING = "is_playing"
        private const val QUIZ_FRAGMENT_TAG = "Quiz"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                back_button.scaleX = 0f
                back_button.scaleY = 0f
            }

            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                ViewCompat.animate(back_button)
                    .setStartDelay(startDelay)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
            }
        })
        if (!isLogged()) requestLogin { }
    }

    private fun populate(categoryId: String) {
        category = db.getCategoryById(categoryId)
        setTheme(category.theme.style)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, category.theme.primaryDarkColor)
        }
        initLayout()
    }

    private fun initLayout() {
        setContentView(R.layout.activity_quiz)
        val packageName = packageName +
                if (InstantApps.isInstantApp(this)) ".quiz" else ""
        val drawableRes = resources.getIdentifier(
            "$CATEGORY_IMAGE_PREFIX${category.id}",
            "drawable",
            packageName
        )
        image_view.setImageResource(drawableRes)
        ViewCompat.animate(image_view)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setInterpolator(interpolator)
            .setStartDelay(300L)
            .start()
        with(play_floating) {
            setImageResource(R_base.drawable.ic_play)
            if (isPlaying) hide() else show()
            setOnClickListener { startQuiz() }
        }
        back_button.setOnClickListener { onBackPressed() }
        title_text.text = category.name
        title_text.setTextColor(ContextCompat.getColor(this, category.theme.textPrimaryColor))
    }

    private fun startQuiz() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
