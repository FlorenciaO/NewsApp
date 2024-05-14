package com.example.newsapp.data.remote.dto

import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.User.Address
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("firstname")
    val firstName: String,
    @SerializedName("lastname")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("birthDate")
    val birthDate: String,
    @SerializedName("address")
    val addressDto: AddressDto,
    @SerializedName("company")
    val companyDto: CompanyDto,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("website")
    val website: String
) {

    data class AddressDto(
        @SerializedName("street")
        val street: String,
        @SerializedName("suite")
        val suite: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("zipcode")
        val zipCode: String,
        @SerializedName("geo")
        val geoCoordinates: GeoDto,
    ) {
        data class GeoDto(
            @SerializedName("lat")
            val lat: String,
            @SerializedName("lng")
            val lng: String
        )

        fun toAddress(): Address {
            return Address(
                street = street,
                city = city,
                lat = geoCoordinates.lat.toDouble(),
                lng = geoCoordinates.lng.toDouble()
            )
        }
    }

    data class CompanyDto(
        @SerializedName("name")
        val name: String,
        @SerializedName("catchPhrase")
        val catchPhrase: String,
        @SerializedName("bs")
        val business: String
    )

    fun toUserEntity(): UserEntity {
        return UserEntity(
            id = id,
            userName = "$firstName $lastName",
            companyName = companyDto.name,
            address = addressDto.toAddress()
        )
    }
}
