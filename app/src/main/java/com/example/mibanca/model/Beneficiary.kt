package com.curso.mibanca.model

import com.google.gson.annotations.SerializedName

data class Beneficiary(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("bankName") val bankName: String
)

data class AddBeneficiaryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("bankName") val bankName: String
)