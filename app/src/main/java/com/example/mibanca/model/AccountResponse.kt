package com.curso.mibanca.model

import com.google.gson.annotations.SerializedName

data class AccountResponse(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("balance") val balanceInCents: Long,
    @SerializedName("createdAt") val createdAt: String
) {
    fun getFormattedBalance(): String {
        return String.format("$%.2f", balanceInCents / 100.0)
    }
}