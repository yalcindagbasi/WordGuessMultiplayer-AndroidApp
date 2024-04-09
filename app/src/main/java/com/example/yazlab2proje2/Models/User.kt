package com.example.yazlab2proje2.Models

import java.util.Date

data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val createdAt: Date = Date(), // or your default date
    val state : UserState = UserState.UNKNOWN,
    val roomType : RoomType = RoomType.NOT,
    val gameId :String = "",
    val receivedInvites : MutableList<String> = mutableListOf(),
    val sentInvites : MutableList<String> = mutableListOf()
)
enum class UserState{
    ONLINE,
    OFFLINE,
    INROOM,
    INGAME,
    UNKNOWN
}
enum class RoomType{
    type1length4,
    type1length5,
    type1length6,
    type1length7,
    type2length4,
    type2length5,
    type2length6,
    type2length7,
    NOT
}