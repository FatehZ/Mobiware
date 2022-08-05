package com.ktxdevelopment.mobiware.models.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ktxdevelopment.mobiware.models.rest.product.Data

@Entity(tableName = "mobile_table")
class RoomPhoneModel (
    @PrimaryKey(autoGenerate = false)
    var slug: String,
    var data: Data,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Data::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(slug)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomPhoneModel> {
        override fun createFromParcel(parcel: Parcel): RoomPhoneModel {
            return RoomPhoneModel(parcel)
        }

        override fun newArray(size: Int): Array<RoomPhoneModel?> {
            return arrayOfNulls(size)
        }
    }
}