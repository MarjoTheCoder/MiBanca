package com.example.mibanca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mibanca.di.NetworkModule
import com.example.mibanca.model.FundRequest
import com.example.mibanca.model.TransactionRequest
import com.example.mibanca.model.ApiErrorBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import okhttp3.ResponseBody

class BankingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<OperationUiState>(OperationUiState.Idle)
    val uiState: StateFlow<OperationUiState> = _uiState

    private val apiService = NetworkModule.apiService
    private val gson = Gson()

    fun transferir(beneficiaryId: String, amountInCents: Long, concepto: String?) {
        _uiState.value = OperationUiState.Loading

        viewModelScope.launch {
            try {
                val request = TransactionRequest(
                    toBeneficiaryId = beneficiaryId,
                    amount = amountInCents,
                    description = if (concepto.isNullOrBlank()) null else concepto
                )

                val response = withContext(Dispatchers.IO) {
                    apiService.makeTransfer(request)
                }

                procesarRespuesta(response, "¡Transferencia realizada con éxito!")
            } catch (e: Exception) {
                _uiState.value = OperationUiState.Error("Error de red: ${e.localizedMessage ?: "Conexión inestable"}")
            }
        }
    }

    fun fondearCuenta(amountInCents: Long) {
        _uiState.value = OperationUiState.Loading

        viewModelScope.launch {
            try {
                val request = FundRequest(amount = amountInCents)
                val response = withContext(Dispatchers.IO) {
                    apiService.fundAccount(request)
                }

                procesarRespuesta(response, "¡Fondeo exitoso!")
            } catch (e: Exception) {
                _uiState.value = OperationUiState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    private fun procesarRespuesta(response: Response<ResponseBody>, mensajeExito: String) {
        if (response.isSuccessful) {
            _uiState.value = OperationUiState.Success
        } else {
            val errorBodyString = response.errorBody()?.string()
            val errorMessage = parsearErrorApi(errorBodyString, response.code())
            _uiState.value = OperationUiState.Error(errorMessage)
        }
    }

    private fun parsearErrorApi(errorBodyStr: String?, statusCode: Int): String {
        if (errorBodyStr.isNullOrEmpty()) return "Error del servidor ($statusCode)"
        return try {
            val apiError = gson.fromJson(errorBodyStr, ApiErrorBody::class.java)
            apiError.message // Retorna la descripción legible en español provista por la API (Pág. 2)
        } catch (e: Exception) {
            "Error inesperado ($statusCode)"
        }
    }

    fun resetState() {
        _uiState.value = OperationUiState.Idle
    }
}