package com.ktxdevelopment.mobiware.models.local

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class LocalUser(
    var userId: String = "",
    var username: String = "",
    var mobileNumberBase: String = "",
    var mobileCountryCode: String = "",
    var mobileId: List<String> = ArrayList(),
    var email: String = "",
    var image: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(mobileNumberBase)
        parcel.writeString(mobileCountryCode)
        parcel.writeStringList(mobileId)
        parcel.writeString(email)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalUser> {
        override fun createFromParcel(parcel: Parcel): LocalUser {
            return LocalUser(parcel)
        }

        override fun newArray(size: Int): Array<LocalUser?> {
            return arrayOfNulls(size)
        }
    }
}