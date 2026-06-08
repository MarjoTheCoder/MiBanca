package com.example.mibanca.network

import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.model.FundRequest
import com.example.mibanca.model.Transaction
import com.example.mibanca.model.TransactionRequest
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE

data class FundAccountRequest(
    @SerializedName("amount") val amountInCents: Long
)
interface BankApiService {

    @PUT("account")
    suspend fun fundAccount(@Body body: FundRequest): Response<ResponseBody>

    @POST("transaction") // Corregido a singular bajo la especificación de la Pág. 8
    suspend fun makeTransfer(@Body body: TransactionRequest): Response<ResponseBody>

    @GET("account")
    suspend fun getAccount(): Response<AccountResponse>

    @POST("account")
    suspend fun createAccount(): Response<AccountResponse>

    @GET("beneficiaries")
    suspend fun getBeneficiaries(): Response<List<Beneficiary>>

    @POST("beneficiaries")
    suspend fun addBeneficiary(
        @Body request: AddBeneficiaryRequest
    ): Response<Beneficiary>

    @PUT("beneficiaries/{id}")
    suspend fun updateBeneficiary(
        @Path("id") id: String,
        @Body request: AddBeneficiaryRequest
    ): Response<Beneficiary>

    @PUT("account")
    suspend fun fundAccount(
        @Body request: FundAccountRequest
    ): Response<AccountResponse>

    @DELETE("beneficiaries/{id}")
    suspend fun deleteBeneficiary(
        @Path("id") id: String
    ): Response<Unit>

    @GET("transactions")
    suspend fun getTransactionHistory(): Response<List<Transaction>>

    @POST("transactions/transfer")
    suspend fun makeTransfer(
        @Body body: Map<String, Any>
    ): Response<Transaction>


}