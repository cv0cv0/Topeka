package me.gr.topeka.base.data

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.auth.api.credentials.Credential
import me.gr.topeka.base.common.TAG

data class Player(
    var firstName: String?,
    var lastInitial: String?,
    var avatar: Avatar?,
    val email: String? = TAG
) : Parcelable {
    constructor(credential: Credential) : this(
        credential.name?.substringBefore(" "),
        credential.name?.substringAfterLast(" ")?.get(0).toString(),
        Avatar.ELEVEN,
        credential.id
    )

    fun valid() = !(firstName.isNullOrEmpty() || lastInitial.isNullOrEmpty()) && avatar != null

    override fun toString(): String {
        return "$firstName $lastInitial"
    }

    fun toCredential(): Credential = Credential.Builder(email)
        .setName(toString())
        .build()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastInitial)
        avatar?.run { parcel.writeInt(ordinal) }
        parcel.writeString(email)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(
                parcel.readString(),
                parcel.readString(),
                Avatar.values()[parcel.readInt()],
                parcel.readString()
            )
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}