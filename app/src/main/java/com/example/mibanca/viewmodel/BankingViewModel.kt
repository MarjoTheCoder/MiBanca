package com.example.mibanca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mibanca.data.repository.BankingRepository
import com.example.mibanca.data.repository.BankingRepositoryImpl
import com.example.mibanca.di.NetworkModule
import com.example.mibanca.model.AccountResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- 1. ESTADOS PARA EL HOMEFRAGMENT ---
sealed class AccountUiState {
    object Loading : AccountUiState()
    data class Success(val account: AccountResponse) : AccountUiState()
    data class Error(val message: String) : AccountUiState()
}

// ⚠️ NOTA: ELIMINAMOS EL BLOQUE DE 'OperationUiState' DE AQUÍ
// PORQUE YA EXISTE EN SU PROPIO ARCHIVO 'OperationUiState.kt'

class BankingViewModel(
    private val repository: BankingRepository = BankingRepositoryImpl(NetworkModule.apiService)
) : ViewModel() {

    // Canal del Home (Cuenta)
    private val _accountState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val accountState: StateFlow<AccountUiState> = _accountState.asStateFlow()

    // Canal de la Transferencia (Utiliza el OperationUiState del archivo original)
    private val _uiState = MutableStateFlow<OperationUiState>(OperationUiState.Idle)
    val uiState: StateFlow<OperationUiState> = _uiState.asStateFlow()

    init {
        obtenerDatosDeCuenta()
    }

    // Método para traer saldo y tarjetas
    fun obtenerDatosDeCuenta() {
        viewModelScope.launch {
            _accountState.value = AccountUiState.Loading
            try {
                val account = NetworkModule.apiService.getAccount()
                _accountState.value = AccountUiState.Success(account)
            } catch (e: Exception) {
                _accountState.value = AccountUiState.Error(e.message ?: "Error desconocido de red")
            }
        }
    }

    fun transferir(targetAccountId: String, amountInCents: Long, concepto: String) {
        viewModelScope.launch {
            _uiState.value = OperationUiState.Loading
            try {
                // 🌟 CORREGIDO: Ahora sí le pasamos el concepto como tercer argumento al repositorio
                val response = repository.makeTransfer(targetAccountId, amountInCents, concepto)

                if (response.isSuccessful) {
                    _uiState.value = OperationUiState.Success
                    obtenerDatosDeCuenta()
                } else {
                    val rawError = response.errorBody()?.string() ?: "Cuerpo de error vacío"
                    android.util.Log.e("ERROR_SERVER_BANCO", "Código HTTP: ${response.code()} | JSON: $rawError")
                    _uiState.value = OperationUiState.Error("Error en servidor")
                }
            } catch (e: Exception) {
                _uiState.value = OperationUiState.Error("Fallo de red: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = OperationUiState.Idle
    }
}