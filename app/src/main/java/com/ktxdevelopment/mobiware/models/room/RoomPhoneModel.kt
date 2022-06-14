package com.ktxdevelopment.mobiware.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ktxdevelopment.mobiware.models.rest.product.Data

@Entity(tableName = "mobile_table")
class RoomPhoneModel (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var data: Data)