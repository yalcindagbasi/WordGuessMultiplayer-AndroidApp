package com.example.yazlab2proje2.Models

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: Long // veya tarih formatı
)
