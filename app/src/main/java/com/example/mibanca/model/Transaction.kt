package com.curso.mibanca.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amountInCents: Long,
    @SerializedName("description") val description: String,
    @SerializedName("createdAt") val createdAt: String
) {
    fun getFormattedAmount(): String {
        return String.format("$%.2f", amountInCents / 100.0)
    }
}