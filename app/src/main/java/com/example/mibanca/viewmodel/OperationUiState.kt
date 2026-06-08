package com.example.mibanca.viewmodel

sealed class OperationUiState {
    object Idle : OperationUiState()
    object Loading : OperationUiState()
    object Success : OperationUiState()
    data class Error(val message: String) : OperationUiState()
}