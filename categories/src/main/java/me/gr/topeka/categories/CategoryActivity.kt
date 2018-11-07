package me.gr.topeka.categories

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.transaction
import kotlinx.android.synthetic.main.activity_category.*
import me.gr.topeka.base.data.Player
import me.gr.topeka.base.extension.db
import me.gr.topeka.base.helper.*
import me.gr.topeka.categories.ui.CategoryFragment

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        requestLogin { updatePlayer(it) }
        if (savedInstanceState == null) {
            with(supportFragmentManager) {
                transaction {
                    replace(
                        R.id.category_container,
                        findFragmentById(R.id.category_container) ?: CategoryFragment()
                    )
                }
            }
        }
        progress_circular.isGone = true
        supportPostponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        score_text.text = getString(me.gr.topeka.base.R.string.x_points, db.getScore())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_category, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.sign_out -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.exitTransition = TransitionInflater.from(this)
                    .inflateTransition(R.transition.category_enter)
            }
            logout()
            launchSignIn(true)
            supportFinishAfterTransition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
        when (requestCode) {
            REQUEST_LOGIN, REQUEST_SAVE -> onSmartLockResult(
                requestCode,
                resultCode,
                data,
                success = {},
                failure = { requestLogin { updatePlayer(it) } }
            )
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    private fun updatePlayer(player: Player) {
        title_text.text = player.toString()
        player.avatar?.let { avatar_view.setAvatar(it.resId) }
    }
}