package com.example.mibanca.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id") val id: String? = "",
    @SerializedName("amount") val amountInCents: Long = 0L,
    @SerializedName("description") val description: String? = "",
    @SerializedName("date") val date: Any? = null,
    @SerializedName("status") val status: String? = "",
    @SerializedName("toAccount") val toAccount: String? = "",
    @SerializedName("fromAccount") val fromAccount: String? = ""
) {
    fun getFormattedAmount(): String {
        return String.format("$%d", amountInCents)
    }
}