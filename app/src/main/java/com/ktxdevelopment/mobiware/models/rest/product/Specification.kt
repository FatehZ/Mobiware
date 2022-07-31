package com.ktxdevelopment.mobiware.models.rest.product

import android.os.Parcel
import android.os.Parcelable

data class Specification(
    val specs: List<Spec>,
    val title: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Spec)!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(specs)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Specification> {
        override fun createFromParcel(parcel: Parcel): Specification {
            return Specification(parcel)
        }

        override fun newArray(size: Int): Array<Specification?> {
            return arrayOfNulls(size)
        }
    }
}