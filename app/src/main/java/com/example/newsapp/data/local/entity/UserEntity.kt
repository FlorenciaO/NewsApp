package com.example.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.data.local.entity.UserEntity.Companion.TABLE_NAME
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.model.User.Address

@Entity(tableName = TABLE_NAME)
data class UserEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo("user_name")
    val userName: String,
    @ColumnInfo("company_name")
    val companyName: String,
    @ColumnInfo("address")
    val address: Address,
) {

    companion object {
        const val TABLE_NAME = "users_table"
    }

    fun toUser(): User {
        return User(
            id = id,
            userName = userName,
            companyName = companyName,
            address = address
        )
    }
}
