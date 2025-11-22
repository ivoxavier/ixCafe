package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.EmpregadosProfile
import com.ixsvf.ixcafe.services.repository.EmpregadosRepository
import com.ixsvf.ixcafe.services.repository.remote.CryptoUtils
import com.ixsvf.ixcafe.services.repository.remote.FirebaseDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmpregadosRepository

    private val _currentUser = MutableStateFlow<EmpregadosProfile?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState = _loginState.asStateFlow()

    init {
        val database = (application as IxCafeApplication).database
        // Não precisamos da API aqui, só do Room, mas o construtor pede os dois
        repository = EmpregadosRepository(database.empregadosDao(), FirebaseDataSource())
    }

    fun loadUser(id: String) {
        viewModelScope.launch {
            val user = repository.getEmpregadoById(id)
            _currentUser.value = user
        }
    }

    fun validatePin(inputPin: String) {
        val user = _currentUser.value ?: return

        // Se não houver pinHash (ex: utilizador criado sem pin), falha ou aceita '0000' (decisão sua)
        if (user.pinHash == null) {
            _loginState.value = AuthState.Error
            return
        }

        val hashedInput = CryptoUtils.hashPin(inputPin)

        if (hashedInput == user.pinHash) {
            _loginState.value = AuthState.Success
        } else {
            _loginState.value = AuthState.Error
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Success : AuthState()
    object Error : AuthState()
}