package com.example.mibanca.model

data class User(
    val uid: String? = null,
    val nombre: String? = null,
    val email: String? = null,
    val saldo: Double = 0.0
)