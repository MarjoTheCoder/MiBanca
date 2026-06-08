package com.example.mibanca.model

import com.google.gson.annotations.SerializedName

data class Beneficiary(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("alias") val alias: String?,
    @SerializedName("accountNumber") val accountNumber: String?,
    @SerializedName("bankName") val bankName: String?
) : java.io.Serializable {

    // Comparación de los objetos usando solo el ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Beneficiary) return false
        return id == other.id
    }

    // Calculamos el hash usando únicamente el ID.
    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class AddBeneficiaryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("alias") val alias: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("bankName") val bankName: String
)