package me.gr.topeka.base.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.include_avatars.*
import kotlinx.android.synthetic.main.include_floating.*
import kotlinx.android.synthetic.main.include_sign_in.*
import me.gr.topeka.base.R
import me.gr.topeka.base.data.Avatar
import me.gr.topeka.base.data.Player
import me.gr.topeka.base.helper.*

class SignInFragment : Fragment() {
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
            val selectedAvatarIndex = getInt(SELECTED_AVATAR_INDEX, -1)
            if (selectedAvatarIndex != -1) {
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
    ): View? {
        val contentView=inflater.inflate(R.layout.fragment_sign_in,container,false)
        return contentView
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
        avatar_grid.run {
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