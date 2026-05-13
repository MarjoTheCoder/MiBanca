package com.example.mibanca.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mibanca.data.AuthManager
class AuthViewModel : ViewModel() {
    private val authManager = AuthManager()
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun login(email: String, pass: String) {
        _errorMessage.value = null
        authManager.login(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _loginResult.value = true
            } else {
                val error = task.exception?.localizedMessage ?: "Error de autenticación desconocido"
                _errorMessage.value = error
                _loginResult.value = false
            }
        }
    }

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    fun register(email: String, pass: String) {
        _errorMessage.value = null

        authManager.register(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _registerResult.value = true
            } else {
                _errorMessage.value = task.exception?.localizedMessage ?: "Error al crear la cuenta"
                _registerResult.value = false
            }
        }
    }
}