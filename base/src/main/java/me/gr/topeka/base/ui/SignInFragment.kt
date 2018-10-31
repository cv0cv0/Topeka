package me.gr.topeka.base.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import me.gr.topeka.base.data.Avatar
import me.gr.topeka.base.data.Player
import me.gr.topeka.base.helper.isLogged
import me.gr.topeka.base.helper.login

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
                selectedAvatar=Avatar.values()[selectedAvatarIndex]
            }
        }
        activity?.run {
            if (isLogged()){
                navigateToCategory()
            }else{

            }
        }
        super.onCreate(savedInstanceState)
    }

    private fun navigateToCategory(){

    }
}