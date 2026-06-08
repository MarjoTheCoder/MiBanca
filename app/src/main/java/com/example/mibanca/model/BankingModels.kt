package com.example.mibanca.model

import com.google.gson.annotations.SerializedName

// Request para fondear la cuenta (Pág. 5)
data class FundRequest(
    @SerializedName("amount") val amount: Long
)

// Request para realizar una transferencia (Pág. 8)
data class TransactionRequest(
    @SerializedName("toBeneficiaryId") val toBeneficiaryId: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("description") val description: String? = null
)

// Modelo para interpretar los errores estructurados de la API (Pág. 2)
data class ApiErrorBody(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String
)