package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.MesasRepository
import com.ixsvf.ixcafe.services.repository.model.Mesa
import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TableManagementUiState(
    val isLoading: Boolean = false,
    val mesas: List<Mesa> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class TableManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MesasRepository
    private val _uiState = MutableStateFlow(TableManagementUiState())
    val uiState: StateFlow<TableManagementUiState> = _uiState.asStateFlow()

    init {
        val db = (application as IxCafeApplication).database
        // Use RetrofitClient for Admin operations (requires Local API)
        val api = RetrofitClient.create(LocalApiInterface::class.java)
        repository = MesasRepository(db.mesaDao(), api)

        observeMesas()
    }

    private fun observeMesas() {
        viewModelScope.launch {
            repository.mesas
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { mesas ->
                    _uiState.update { it.copy(mesas = mesas) }
                }
        }
    }

    fun criarMesa(numero: String, capacidade: String, localizacao: String) {
        val numInt = numero.toIntOrNull()
        val capInt = capacidade.toIntOrNull()

        if (numInt == null || capInt == null) {
            _uiState.update { it.copy(error = "Número e Capacidade devem ser numéricos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                repository.criarMesa(numInt, capInt, localizacao)
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "Mesa $numInt criada com sucesso!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Erro: ${e.message}")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}