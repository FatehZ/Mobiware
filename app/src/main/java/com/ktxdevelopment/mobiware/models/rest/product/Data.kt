package com.ktxdevelopment.mobiware.models.rest.product

import android.os.Parcel
import android.os.Parcelable


data class Data(
    val brand: String,
    val dimension: String,
    val os: String,
    val phone_images: List<String>,
    val phone_name: String,
    val release_date: String,
    val specifications: List<Specification>,
    val storage: String,
    val thumbnail: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Specification)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brand)
        parcel.writeString(dimension)
        parcel.writeString(os)
        parcel.writeStringList(phone_images)
        parcel.writeString(phone_name)
        parcel.writeString(release_date)
        parcel.writeTypedList(specifications)
        parcel.writeString(storage)
        parcel.writeString(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}