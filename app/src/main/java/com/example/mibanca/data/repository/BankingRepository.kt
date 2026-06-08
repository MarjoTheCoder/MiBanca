package com.example.mibanca.data.repository

import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.model.Transaction
import com.example.mibanca.model.AddBeneficiaryRequest // Importamos el request
import retrofit2.Response

interface BankingRepository {
    suspend fun getAccount(): Response<AccountResponse>
    suspend fun createAccount(): Response<AccountResponse>

    suspend fun getBeneficiaries(): Response<List<Beneficiary>>
    suspend fun addBeneficiary(name: String, lastName: String, alias: String, accountNumber: String, bankName: String): Response<Beneficiary>

    suspend fun updateBeneficiary(id: String, request: AddBeneficiaryRequest): Response<Beneficiary>
    suspend fun deleteBeneficiary(id: String): Response<Unit>

    suspend fun getTransactionHistory(): Response<List<Transaction>>
    suspend fun makeTransfer(targetAccountId: String, amountInCents: Long): Response<Transaction>
}