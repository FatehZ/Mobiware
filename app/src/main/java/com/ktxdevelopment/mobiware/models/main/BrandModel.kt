package com.ktxdevelopment.mobiware.models.main

import android.os.Parcel
import android.os.Parcelable

data class BrandModel(
    val brand_id: Int,
    val brand_name: String,
    val brand_slug: String,
    val detail: String,
    val device_count: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(brand_id)
        parcel.writeString(brand_name)
        parcel.writeString(brand_slug)
        parcel.writeString(detail)
        parcel.writeInt(device_count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BrandModel> {
        override fun createFromParcel(parcel: Parcel): BrandModel {
            return BrandModel(parcel)
        }

        override fun newArray(size: Int): Array<BrandModel?> {
            return arrayOfNulls(size)
        }
    }
}