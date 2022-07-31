package com.ktxdevelopment.mobiware.models.firebase

import android.os.Parcel
import android.os.Parcelable

data class FireUser(
    var userId: String = "",
    var username: String = "",
    var imageUrl: String = "",
    var mobileNumberCode: String = "",
    var mobileNumberBase: String = "",
    var mobileId: List<String> = ArrayList(),
    var email: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(imageUrl)
        parcel.writeString(mobileNumberCode)
        parcel.writeString(mobileNumberBase)
        parcel.writeStringList(mobileId)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FireUser> {
        override fun createFromParcel(parcel: Parcel): FireUser {
            return FireUser(parcel)
        }

        override fun newArray(size: Int): Array<FireUser?> {
            return arrayOfNulls(size)
        }
    }
}