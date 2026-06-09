package com.example.mibanca.network

import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.model.FundRequest
import com.example.mibanca.model.Transaction
import com.example.mibanca.model.TransactionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface BankApiService {
    @GET("account")
    suspend fun getAccount(): AccountResponse

    @POST("account")
    suspend fun createAccount(): AccountResponse

    @PUT("account")
    suspend fun fundAccount(@Body body: FundRequest): AccountResponse

    @GET("beneficiaries")
    suspend fun getBeneficiaries(): List<Beneficiary>

    @POST("beneficiaries")
    suspend fun addBeneficiary(@Body request: AddBeneficiaryRequest): Beneficiary

    @POST("transaction")
    suspend fun makeTransfer(@Body body: TransactionRequest): Response<Unit>

    @GET("transaction")
    suspend fun getTransactionHistory(): List<Transaction>
}