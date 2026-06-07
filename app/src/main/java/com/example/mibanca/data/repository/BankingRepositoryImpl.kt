package com.curso.mibanca.data.repository

import com.curso.mibanca.model.AccountResponse
import com.curso.mibanca.model.AddBeneficiaryRequest
import com.curso.mibanca.model.Beneficiary
import com.curso.mibanca.model.Transaction
import com.curso.mibanca.network.BankApiService
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
        accountNumber: String,
        bankName: String
    ): Response<Beneficiary> {
        val request = AddBeneficiaryRequest(name, accountNumber, bankName)
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