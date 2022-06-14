package com.ktxdevelopment.mobiware.models.rest.product

import android.os.Parcel
import android.os.Parcelable

data class Spec(
    val key: String,
    val `val`: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeStringList(`val`)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Spec> {
        override fun createFromParcel(parcel: Parcel): Spec {
            return Spec(parcel)
        }

        override fun newArray(size: Int): Array<Spec?> {
            return arrayOfNulls(size)
        }
    }
}