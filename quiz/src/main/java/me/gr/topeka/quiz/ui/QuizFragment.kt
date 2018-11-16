package me.gr.topeka.quiz.ui

import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterViewAnimator
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import me.gr.topeka.base.data.Category
import me.gr.topeka.base.extension.db
import me.gr.topeka.base.helper.requestLogin
import me.gr.topeka.base.widget.AvatarView
import me.gr.topeka.quiz.R
import me.gr.topeka.quiz.adapter.QuizAdapter
import me.gr.topeka.quiz.adapter.ScoreAdapter
import me.gr.topeka.quiz.widget.AbsQuizView
import me.gr.topeka.quiz.widget.SolveStateListener

class QuizFragment : Fragment() {
    val quizView by lazy(LazyThreadSafetyMode.NONE) {
        view!!.findViewById<AdapterViewAnimator>(R.id.quiz_view)
    }
    private val progressText by lazy(LazyThreadSafetyMode.NONE) {
        view!!.findViewById<TextView>(R.id.progress_text)
    }
    private val progressBar by lazy(LazyThreadSafetyMode.NONE) {
        view!!.findViewById<ProgressBar>(R.id.progress_horizontal).apply {
            max = category.quizzes.size
        }
    }
    private val quizAdapter by lazy(LazyThreadSafetyMode.NONE) {
        QuizAdapter(context!!, category)
    }
    private val scoreAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ScoreAdapter(context!!, category)
    }
    private val category by lazy(LazyThreadSafetyMode.NONE) {
        activity!!.db.getCategoryById(arguments!!.getString(Category.ID)!!)
    }
    private var solveStateListener: SolveStateListener? = null

    companion object {
        private const val USER_INPUT = "USER_INPUT"

        fun newInstance(
            categoryId: String,
            solveStateListener: SolveStateListener?
        ) = QuizFragment().apply {
            this.solveStateListener = solveStateListener
            arguments = Bundle().apply { putString(Category.ID, categoryId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = ContextThemeWrapper(activity, category.theme.style)
        return LayoutInflater.from(context).inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setProgress(category.firstUnsolvedQuizPosition)
        decideViewDisplay()
        setQuizViewAnimation()
        setAvatarDrawable()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val focusChild = quizView.focusedChild
        if (focusChild is ViewGroup) {
            val currentView = focusChild.getChildAt(0)
            if (currentView is AbsQuizView<*>) {
                outState.putBundle(USER_INPUT, currentView.userInput)
            }
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            quizView.doOnNextLayout {
                val currentView = (it as ViewGroup).getChildAt(0)
                if (currentView is ViewGroup) {
                    val child = currentView.getChildAt(0)
                    if (child is AbsQuizView<*>) {
                        child.userInput = savedInstanceState.getBundle(USER_INPUT) ?: Bundle.EMPTY
                    }
                }
            }
        }
        super.onViewStateRestored(savedInstanceState)
    }

    fun showNextPage(): Boolean {
        val nextItem = quizView.displayedChild + 1
        if (nextItem < quizView.adapter.count) {
            setProgress(nextItem)
            quizView.showNext()
            activity!!.db.updateCategory(category)
            return true
        }
        category.solved = true
        activity!!.db.updateCategory(category)
        return false
    }

    fun hasSolveStateListener()=solveStateListener!=null

    fun setSolveStateListener(solveStateListener: SolveStateListener){
        this.solveStateListener=solveStateListener
        if (category.solved){
            solveStateListener.onSolved()
        }
    }

    private fun setAvatarDrawable() {
        val avatarView = view!!.findViewById<AvatarView>(R.id.avatar_view)
        activity?.requestLogin { player ->
            if (player.valid()) {
                avatarView.setAvatar(player.avatar!!.resId)
                with(ViewCompat.animate(avatarView)) {
                    interpolator = FastOutLinearInInterpolator()
                    startDelay = 500
                    scaleX(1f)
                    scaleY(1f)
                    start()
                }
            }
        }
    }

    private fun setQuizViewAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            quizView.setInAnimation(activity, R.animator.slide_in_bottom)
            quizView.setOutAnimation(activity, R.animator.slide_out_top)
        }
    }

    private fun decideViewDisplay() {
        if (category.solved) {
            with(view!!.findViewById<ListView>(R.id.score_list)) {
                adapter = scoreAdapter
                isGone = false
            }
            quizView.isGone = true
        } else {
            quizView.adapter = quizAdapter
            quizView.setSelection(category.firstUnsolvedQuizPosition)
        }
    }

    private fun setProgress(position: Int) {
        if (isAdded) {
            progressBar.progress = position
            progressText.text = getString(me.gr.topeka.base.R.string.quiz_of_quizzes, position, category.quizzes.size)
        }
    }
}