package com.example.newsapp.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.newsapp.domain.model.User.Address
import com.example.newsapp.utils.JsonParser
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun fromAddressJson(json: String): Address {
        return jsonParser.fromJson<Address>(
            json,
            object : TypeToken<Address>(){}.type
        ) ?: Address("","",0.0, 0.0)
    }

    @TypeConverter
    fun toAddressJson(address: Address): String {
        return jsonParser.toJson(
            address,
            object : TypeToken<Address>(){}.type
        ) ?: ""
    }
}
