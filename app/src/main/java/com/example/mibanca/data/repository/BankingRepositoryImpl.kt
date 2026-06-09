package com.example.mibanca.data.repository

import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.AddBeneficiaryRequest
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.model.Transaction
import com.example.mibanca.model.TransactionRequest
import com.example.mibanca.network.BankApiService
import retrofit2.Response

class BankingRepositoryImpl(
    private val apiService: BankApiService
) : BankingRepository {

    override suspend fun getAccount(): AccountResponse {
        return apiService.getAccount()
    }

    override suspend fun getBeneficiaries(): List<Beneficiary> {
        return apiService.getBeneficiaries()
    }

    override suspend fun addBeneficiary(request: AddBeneficiaryRequest): Beneficiary {
        return apiService.addBeneficiary(request)
    }

    override suspend fun makeTransfer(targetAccountId: String, amountInCents: Long, concepto: String): Response<Unit> {
        val request = TransactionRequest(
            toBeneficiaryId = targetAccountId,
            amount = amountInCents,
            description = concepto
        )
        return apiService.makeTransfer(request)
    }

    override suspend fun getTransactionHistory(): List<Transaction> {
        return apiService.getTransactionHistory()
    }
}