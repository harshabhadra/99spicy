package com.a99Spicy.a99spicy.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a99Spicy.a99spicy.domain.DeliveryAddress
import com.squareup.moshi.Json

@Entity(tableName = "address_table")
data class DatabaseShipping(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 1,
    var firstName: String,
    var lastName: String,
    var company: String,
    var address1: String,
    var address2: String,
    var city: String,
    var postcode: String,
    var country: String,
    var state: String
)

fun List<DatabaseShipping>.asDomainDeliveryAddress():List<DeliveryAddress>{
    return map {
        DeliveryAddress(
            id = it.id,
            firstName = it.firstName,
            lastName = it.lastName,
            company = it.company,
            address1 = it.address1,
            address2 = it.address2,
            city = it.city,
            postcode = it.postcode,
            country = it.country,
            state = it.state
        )
    }
}