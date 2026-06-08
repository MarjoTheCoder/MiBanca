package com.example.mibanca.data.repository

import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.model.Transaction
import com.example.mibanca.network.BankApiService
import retrofit2.Response

class BankingRepositoryImpl(
    private val apiService: BankApiService
) : BankingRepository {

    override suspend fun getAccount(): Response<AccountResponse> {
        return apiService.getAccount()
    }

    override suspend fun createAccount(): Response<AccountResponse> {
        return apiService.createAccount()
    }

    override suspend fun getBeneficiaries(): Response<List<Beneficiary>> {
        return apiService.getBeneficiaries()
    }

    override suspend fun addBeneficiary(
        name: String,
        lastName: String,
        alias: String,
        accountNumber: String,
        bankName: String
    ): Response<Beneficiary> {
        val request = AddBeneficiaryRequest(name, lastName, alias, accountNumber, bankName)
        return apiService.addBeneficiary(request)
    }

    override suspend fun getTransactionHistory(): Response<List<Transaction>> {
        return apiService.getTransactionHistory()
    }

    override suspend fun makeTransfer(
        targetAccountId: String,
        amountInCents: Long
    ): Response<Transaction> {
        val body = mapOf(
            "targetAccountId" to targetAccountId,
            "amount" to amountInCents
        )
        return apiService.makeTransfer(body)
    }
}