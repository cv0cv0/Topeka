package me.gr.topeka.quiz

import android.animation.*
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.transaction
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.instantapps.InstantApps
import kotlinx.android.synthetic.main.activity_quiz.*
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.data.JsonAttributes
import me.gr.topeka.base.extension.FOREGROUND_COLOR
import me.gr.topeka.base.extension.db
import me.gr.topeka.base.helper.isLogged
import me.gr.topeka.base.helper.launchCategory
import me.gr.topeka.base.helper.onSmartLockResult
import me.gr.topeka.base.helper.requestLogin
import me.gr.topeka.quiz.transition.SharedTextCallback
import me.gr.topeka.quiz.ui.QuizFragment
import me.gr.topeka.quiz.widget.SolveStateListener
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
        with(intent.data!!) {
            if (path!!.startsWith("/quiz")) {
                populate(lastPathSegment!!)
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
        if (isPlaying) {
            image_view.isGone = true
            fragment_container.isInvisible = false
            floating_button.hide()
            setToolbarElevation(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onSmartLockResult(requestCode, resultCode, data, success = {}, failure = { requestLogin { } })
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_PLAYING, floating_button.isGone)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        ViewCompat.animate(back_button)
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(100L)
            .start()
        ViewCompat.animate(image_view)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .alpha(0f)
            .setInterpolator(interpolator)
            .start()
        ViewCompat.animate(floating_button)
            .scaleX(0f)
            .scaleY(0f)
            .setStartDelay(100L)
            .setInterpolator(interpolator)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View?) {
                    super@QuizActivity.onBackPressed()
                }
            })
            .start()
    }

    fun submitAnswer() {
        if (!quizFragment.showNextPage()) {
            setToolbarElevation(true)
            quizFragment.submitAnswer()
            setResult(Activity.RESULT_OK, Intent().putExtra(JsonAttributes.ID, category.id))
        }
    }

    private val solveStateListener = object : SolveStateListener {
        override fun onSolved() {
            setResult(Activity.RESULT_OK, Intent().putExtra(JsonAttributes.ID, category.id))
            with(revealAnimator) {
                if (isRunning) {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            showDoneFab()
                            removeListener(this)
                        }
                    })
                } else {
                    showDoneFab()
                }
            }
        }

        private fun showDoneFab() {
            with(floating_button) {
                id = R_base.id.quiz_done
                isGone = false
                scaleX = 0f
                scaleY = 0f
                setImageResource(R_base.drawable.ic_tick)
            }
            ViewCompat.animate(floating_button)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(interpolator)
                .setListener(null)
                .start()
        }
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
        with(floating_button) {
            setImageResource(R_base.drawable.ic_play)
            if (isPlaying) hide() else show()
            setOnClickListener { startQuiz() }
        }
        back_button.setOnClickListener { onBackPressed() }
        title_text.text = category.name
        title_text.setTextColor(ContextCompat.getColor(this, category.theme.textPrimaryColor))
    }

    private fun startQuiz() {
        quizFragment = QuizFragment.newInstance(category.id, solveStateListener)
        supportFragmentManager.transaction {
            replace(R.id.fragment_container, quizFragment, QUIZ_FRAGMENT_TAG)
        }
        fragment_container.setBackgroundColor(ContextCompat.getColor(this, category.theme.windowBackgroundColor))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            revealAnimator()
        } else {
            image_view.isGone = true
            fragment_container.isInvisible = false
            floating_button.hide()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun revealAnimator() {
        val centerX = (floating_button.left + floating_button.right) / 2
        val centerY = (floating_button.top + floating_button.bottom) / 2 - floating_button.height
        val endRadius = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()
        revealAnimator = ViewAnimationUtils.createCircularReveal(
            fragment_container,
            centerX,
            centerY,
            floating_button.width.toFloat(),
            endRadius
        )
        revealAnimator.interpolator = FastOutLinearInInterpolator()
        revealAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                image_view.isGone = true
                revealAnimator.removeListener(this)
            }
        })

        val accentColor = ContextCompat.getColor(this, category.theme.accentColor)
        colorAnimator = ObjectAnimator.ofInt(
            fragment_container,
            fragment_container.FOREGROUND_COLOR,
            accentColor,
            Color.TRANSPARENT
        )
        colorAnimator.setEvaluator(ArgbEvaluator())
        colorAnimator.interpolator = interpolator

        fragment_container.isGone = false
        with(AnimatorSet()) {
            play(revealAnimator).with(colorAnimator)
            start()
        }

        ViewCompat.animate(floating_button)
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setInterpolator(interpolator)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View?) {
                    setToolbarElevation(false)
                    floating_button.isGone = true
                }
            })
            .start()
    }

    private fun setToolbarElevation(shouldElevate: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.elevation = if (shouldElevate) {
                resources.getDimension(R_base.dimen.elevation_header)
            } else {
                0f
            }
        }
    }
}
