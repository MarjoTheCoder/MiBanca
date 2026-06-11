package com.example.mibanca.model

import com.google.gson.annotations.SerializedName

data class AccountResponse(
    val accountNumber: String,
    val balance: Long,
    val createdAt: Any? = null,
    val ownerId: String
) {
    fun getFormattedBalance(): String {
        return "$ $balance.00"
    }
}