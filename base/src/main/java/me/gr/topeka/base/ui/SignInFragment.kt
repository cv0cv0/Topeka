package me.gr.topeka.base.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.include_avatars.*
import kotlinx.android.synthetic.main.include_floating.*
import kotlinx.android.synthetic.main.include_sign_in.*
import me.gr.topeka.base.R
import me.gr.topeka.base.adapter.AvatarAdapter
import me.gr.topeka.base.data.Avatar
import me.gr.topeka.base.data.Player
import me.gr.topeka.base.helper.*
import me.gr.topeka.base.util.TextWatcherAdapter
import me.gr.topeka.base.util.TransitionListenerAdapter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

class SignInFragment : Fragment(), AnkoLogger {
    private var player: Player? = null
    private var selectedAvatar: Avatar? = null

    private val isEditMode by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getBoolean(EDIT_MODE, false) ?: false
    }

    companion object {
        private const val SELECTED_AVATAR_INDEX = "SELECTED_AVATAR_INDEX"

        const val EDIT_MODE = "EDIT_MODE"

        fun newInstance(isEditMode: Boolean = false) = SignInFragment().apply {
            arguments = Bundle().apply { putBoolean(EDIT_MODE, isEditMode) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.run {
            val selectedAvatarIndex = getInt(SELECTED_AVATAR_INDEX)
            if (selectedAvatarIndex != GridView.INVALID_POSITION) {
                selectedAvatar = Avatar.values()[selectedAvatarIndex]
            }
        }
        activity?.run {
            if (isLogged()) {
                navigateToCategory()
            } else {
                login.login(this, ::onSuccessfulLogin)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sign_in, container, false).apply {
        onLayoutChange {
            with(avatar_grid) {
                adapter = AvatarAdapter()
                numColumns = calculateSpanCount()
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    selectedAvatar = Avatar.values()[position]
                    showDoneFloating()
                }
                selectedAvatar?.let { selectAvatar(it) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (isEditMode || player?.valid() == true) {
            initContentViews()
            initContents()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activity?.onSmartLockResult(requestCode, resultCode, data,
            success = {
                player = it
                initContents()
                navigateToCategory()
            },
            failure = {
                activity?.let {
                    login.login(it, ::onSuccessfulLogin)
                }
            })
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_AVATAR_INDEX, avatar_grid.checkedItemPosition)
        super.onSaveInstanceState(outState)
    }

    private fun initContentViews() {
        val textWatcher = object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) done_floating.hide()
            }

            override fun afterTextChanged(s: Editable) {
                showDoneFloating()
            }
        }

        first_name.addTextChangedListener(textWatcher)
        last_initial.addTextChangedListener(textWatcher)
        done_floating.setOnClickListener {
            val firstName = first_name.text.toString()
            val lastInitial = last_initial.text.toString()
            activity?.let { activity ->
                login.save(activity, Player(firstName, lastInitial, selectedAvatar)) {
                    debug("Saving login info successful.")
                }
            }
            done_floating.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    performSignInTransition(avatar_grid.getChildAt(selectedAvatar!!.ordinal))
                }
            })
        }
    }

    private fun performSignInTransition(v: View? = null) {
        if (v == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            navigateToCategory()
            return
        }

        activity?.run {
            window.sharedElementExitTransition.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    finish()
                }
            })
            val pairs = TransitionHelper.createSafeTransitionParticipants(
                this,
                true,
                Pair(v, getString(R.string.transition_avatar))
            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
            launchCategory(options)
        }
    }

    private fun calculateSpanCount(): Int {
        val avatarSize = resources.getDimensionPixelSize(R.dimen.size_avatar)
        val avatarPadding = resources.getDimensionPixelSize(R.dimen.spacing_double)
        return avatar_grid.width / (avatarSize + avatarPadding)
    }

    private fun initContents() = player?.run {
        if (valid()) {
            first_name.setText(firstName)
            last_initial.setText(lastInitial)
            selectAvatar(avatar!!)
        }
    }

    private fun onSuccessfulLogin(player: Player) {
        if (login != DefaultLogin) return

        this.player = player
        if (isEditMode) {
            with(player) {
                first_name.setText(firstName)
                last_initial.run {
                    setText(lastInitial)
                    requestFocus()
                    setSelection(length())
                }
                activity?.let {
                    login.save(it, this) {
                        avatar?.let(::selectAvatar)
                    }
                }
            }
        } else {
            navigateToCategory()
        }
    }

    private fun selectAvatar(avatar: Avatar) {
        selectedAvatar = avatar
        with(avatar_grid) {
            requestFocusFromTouch()
            setItemChecked(avatar.ordinal, true)
        }
        showDoneFloating()
    }

    private fun showDoneFloating() {
        if (first_name.text.isNotEmpty()
            && last_initial.text.isNotEmpty()
            && selectedAvatar != null
        ) {
            done_floating.show()
        }
    }

    private fun navigateToCategory() = activity?.run {
        launchCategory()
        supportFinishAfterTransition()
    }
}