package com.example.mibanca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mibanca.data.repository.BankingRepository
import com.example.mibanca.data.repository.BankingRepositoryImpl
import com.example.mibanca.di.NetworkModule
import com.example.mibanca.model.AccountResponse
import com.example.mibanca.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AccountUiState {
    object Loading : AccountUiState()
    data class Success(val account: AccountResponse) : AccountUiState()
    data class Error(val message: String) : AccountUiState()
}

sealed class TransactionsUiState {
    object Loading : TransactionsUiState()
    data class Success(val transactions: List<Transaction>) : TransactionsUiState()
    data class Error(val message: String) : TransactionsUiState()
}

class BankingViewModel(
    private val repository: BankingRepository = BankingRepositoryImpl(NetworkModule.apiService)
) : ViewModel() {

    private val _accountState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val accountState: StateFlow<AccountUiState> = _accountState.asStateFlow()

    private val _uiState = MutableStateFlow<OperationUiState>(OperationUiState.Idle)
    val uiState: StateFlow<OperationUiState> = _uiState.asStateFlow()

    private val _transactionsState = MutableStateFlow<TransactionsUiState>(TransactionsUiState.Loading)
    val transactionsState: StateFlow<TransactionsUiState> = _transactionsState.asStateFlow()

    init {
        cargarTodoElHome()
    }

    fun cargarTodoElHome() {
        obtenerDatosDeCuenta()
        obtenerHistorialMovimientos()
    }

    fun obtenerDatosDeCuenta() {
        viewModelScope.launch {
            _accountState.value = AccountUiState.Loading
            try {
                val account = NetworkModule.apiService.getAccount()
                _accountState.value = AccountUiState.Success(account)
            } catch (e: Exception) {
                _accountState.value = AccountUiState.Error(e.message ?: "Error al obtener saldo")
            }
        }
    }

    fun obtenerHistorialMovimientos() {
        viewModelScope.launch {
            _transactionsState.value = TransactionsUiState.Loading
            try {
                val listaMovimientos = repository.getTransactionHistory()
                _transactionsState.value = TransactionsUiState.Success(listaMovimientos)
            } catch (e: Exception) {
                _transactionsState.value = TransactionsUiState.Error(e.message ?: "Error al cargar movimientos")
            }
        }
    }

    fun transferir(targetAccountId: String, amountInCents: Long, concepto: String) {
        viewModelScope.launch {
            _uiState.value = OperationUiState.Loading
            try {
                val response = repository.makeTransfer(targetAccountId, amountInCents, concepto)
                if (response.isSuccessful) {
                    _uiState.value = OperationUiState.Success
                    cargarTodoElHome()
                } else {
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