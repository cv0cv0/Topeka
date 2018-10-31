package me.gr.topeka.base.helper

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.common.api.ResolvableApiException
import me.gr.topeka.base.data.Avatar
import me.gr.topeka.base.data.Player
import me.gr.topeka.base.extension.db

const val REQUEST_LOGIN = 704
const val REQUEST_SAVE = 54

const val TAG = "CredentialsHelper"

const val PLAYER_PREFERENCES = "playerPreferences"
const val PREFERENCE_FIRST_NAME = "$PLAYER_PREFERENCES.firstName"
const val PREFERENCE_LAST_INITIAL = "$PLAYER_PREFERENCES.lastInitial"
const val PREFERENCE_AVATAR = "$PLAYER_PREFERENCES.avatar"
const val PREFERENCE_EMAIL = "$PLAYER_PREFERENCES.email"

var login = DefaultLogin

object DefaultLogin : Login {
    override fun login(activity: Activity, onSuccess: (Player) -> Unit) {
        activity.requestLogin(onSuccess)
    }

    override fun save(activity: Activity, player: Player, onSuccess: () -> Unit) {
        activity.saveLogin(player, onSuccess)
    }
}

fun Activity.requestLogin(success: (Player) -> Unit) {
    if (isLogged()) {
        success(player!!)
        return
    }
    credentialsClient.request(
        CredentialRequest.Builder()
            .setPasswordLoginSupported(true)
            .build()
    ).addOnCompleteListener {
        if (it.isSuccessful) {
            val player = Player(it.result!!.credential)
            storePlayer(player)
            success(player)
        } else {
            resolveException(it.exception, REQUEST_LOGIN)
        }
    }
}

fun Activity.saveLogin(player: Player, success: () -> Unit) {
    storePlayer(player)
    credentialsClient.save(player.toCredential())
        .addOnCompleteListener {
            if (it.isSuccessful) {
                success()
            } else {
                resolveException(it.exception, REQUEST_SAVE)
            }
        }
}

fun Activity.onSmartLockResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    success: (Player) -> Unit,
    failure: () -> Unit
) {
    if (resultCode != RESULT_OK) return

    when (requestCode) {
        REQUEST_LOGIN -> {
            data?.run {
                if (hasExtra(Credential.EXTRA_KEY)) {
                    Player(getParcelableExtra(Credential.EXTRA_KEY)).let {
                        storePlayer(it)
                        success(it)
                    }
                } else {
                    failure()
                }
            }
        }
        REQUEST_SAVE -> Log.d(TAG, "savePlayer result")
    }
}

fun Context.isLogged(): Boolean = with(playerPrefs) {
    contains(PREFERENCE_FIRST_NAME)
            && contains(PREFERENCE_LAST_INITIAL)
            && contains(PREFERENCE_AVATAR)
}

fun Context.logout() {
    playerEdit.clear().apply()
    db.reset()
}

interface Login {
    fun login(activity: Activity, onSuccess: (Player) -> Unit)

    fun save(activity: Activity, player: Player, onSuccess: () -> Unit)
}

private val Context.credentialsClient
    get() = Credentials.getClient(
        this,
        CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()
    )

private val Context.playerPrefs
    get() = getSharedPreferences(PLAYER_PREFERENCES, Context.MODE_PRIVATE)

private val Context.playerEdit
    get() = playerPrefs.edit()

private val Context.player: Player?
    get() = with(playerPrefs) {
        Player(
            getString(PREFERENCE_FIRST_NAME, null),
            getString(PREFERENCE_LAST_INITIAL, null),
            getString(PREFERENCE_AVATAR, null)?.let { Avatar.valueOf(it) },
            getString(PREFERENCE_EMAIL, "")
        )
            .takeIf { it.valid() }
    }

private fun Context.storePlayer(player: Player) = with(player) {
    if (valid()) {
        playerEdit.putString(PREFERENCE_FIRST_NAME, firstName)
            .putString(PREFERENCE_LAST_INITIAL, lastInitial)
            .putString(PREFERENCE_AVATAR, avatar?.name)
            .putString(PREFERENCE_EMAIL, email)
            .apply()
    }
}

private fun Activity.resolveException(exception: Exception?, requestCode: Int) {
    if (exception is ResolvableApiException) {
        Log.d(TAG, "Resolving: $exception")
        try {
            exception.startResolutionForResult(this, requestCode)
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "STATUS: Failed to send resolution.", e)
        }
    }
}