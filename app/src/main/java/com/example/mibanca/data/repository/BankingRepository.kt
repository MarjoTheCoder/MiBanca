package com.curso.mibanca.data.repository

import com.curso.mibanca.model.AccountResponse
import com.curso.mibanca.model.Beneficiary
import com.curso.mibanca.model.Transaction
import retrofit2.Response

interface BankingRepository {
    suspend fun getAccount(): Response<AccountResponse>
    suspend fun createAccount(): Response<AccountResponse>

    suspend fun getBeneficiaries(): Response<List<Beneficiary>>
    suspend fun addBeneficiary(name: String, accountNumber: String, bankName: String): Response<Beneficiary>

    suspend fun getTransactionHistory(): Response<List<Transaction>>
    suspend fun makeTransfer(targetAccountId: String, amountInCents: Long): Response<Transaction>
}