package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.model.EmpregadosProfile
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

        val hashedInput = CryptoUtils.hashPin(inputPin)

        // --- LOGS PARA DEBUG (Veja isto no Logcat) ---
        println("!!! DEBUG AUTH !!!")
        println("User ID: ${user.id}")
        println("User Name: ${user.name}")
        println("PIN da DB (Hash): '${user.pinHash}'") // As plicas '' ajudam a ver espaços escondidos
        println("PIN Inserido (Hash): '$hashedInput'")
        // --------------------------------------------

        if (user.pinHash == null) {
            println("!!! ERRO: O pinHash do utilizador é NULL !!!")
            _loginState.value = AuthState.Error
            return
        }

        if (hashedInput == user.pinHash) {
            println("!!! SUCESSO: PIN Correto !!!")
            _loginState.value = AuthState.Success
        } else {
            println("!!! FALHA: Hashes não coincidem !!!")
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