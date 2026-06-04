package com.curso.mibanca.network

import com.curso.mibanca.model.AccountResponse
import com.curso.mibanca.model.AddBeneficiaryRequest
import com.curso.mibanca.model.Beneficiary
import com.curso.mibanca.model.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BankApiService {

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

    @GET("transactions")
    suspend fun getTransactionHistory(): Response<List<Transaction>>

    @POST("transactions/transfer")
    suspend fun makeTransfer(
        @Body body: Map<String, Any>
    ): Response<Transaction>
}