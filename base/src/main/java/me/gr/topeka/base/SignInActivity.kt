package me.gr.topeka.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import me.gr.topeka.base.helper.isLogged
import me.gr.topeka.base.ui.SignInFragment

class SignInActivity : AppCompatActivity() {
    private val isEditMode
        get() = intent.getBooleanExtra(SignInFragment.EDIT_MODE, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        if (savedInstanceState == null) {
            supportFragmentManager.transaction {
                replace(
                    R.id.sign_in_container,
                    SignInFragment.newInstance(isEditMode || !isLogged())
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        supportFragmentManager.findFragmentById(R.id.sign_in_container)
            ?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        if (isLogged()) finish()
    }
}
