package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.EmpregadosProfile
import com.ixsvf.ixcafe.services.repository.EmpregadosRepository
import com.ixsvf.ixcafe.services.repository.remote.FirebaseDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmpregadosRepository

    // O utilizador atualmente logado
    private val _currentUser = MutableStateFlow<EmpregadosProfile?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Evento de Logout forçado
    private val _shouldLogout = MutableStateFlow(false)
    val shouldLogout = _shouldLogout.asStateFlow()

    private var sessionJob: Job? = null

    init {
        val db = (application as IxCafeApplication).database
        repository = EmpregadosRepository(db.empregadosDao(), FirebaseDataSource())
    }

    // Chamado quando o login é feito com sucesso
    fun startSession(userId: String) {
        // Cancela sessão anterior se existir
        sessionJob?.cancel()

        sessionJob = viewModelScope.launch {
            // Observa a DB local em tempo real para este ID
            repository.getEmpregadoFlow(userId).collectLatest { user ->
                if (user == null) {
                    // O utilizador foi apagado da BD? Logout.
                    _shouldLogout.value = true
                } else if (!user.isActive) {
                    // O utilizador foi desativado? Logout.
                    _shouldLogout.value = true
                } else {
                    // Tudo ok, atualiza o estado
                    _currentUser.value = user
                }
            }
        }
    }

    fun logout() {
        sessionJob?.cancel()
        _currentUser.value = null
        _shouldLogout.value = false
    }

    // Chamado pela UI depois de efetuar o logout, para resetar o trigger
    fun onLogoutCompleted() {
        _shouldLogout.value = false
    }
}