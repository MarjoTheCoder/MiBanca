package com.example.mibanca.data.repository

import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.model.Transaction
import retrofit2.Response

interface BankingRepository {
    suspend fun getAccount(): AccountResponse
    suspend fun getBeneficiaries(): List<Beneficiary>
    suspend fun addBeneficiary(request: AddBeneficiaryRequest): Beneficiary
    suspend fun makeTransfer(targetAccountId: String, amountInCents: Long, concepto: String): Response<Unit>
    suspend fun getTransactionHistory(): List<Transaction>

}