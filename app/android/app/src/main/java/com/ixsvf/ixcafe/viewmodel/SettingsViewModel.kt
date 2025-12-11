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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val mesas: List<Mesa> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val mesasRepository: MesasRepository

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val db = (application as IxCafeApplication).database
        val api = RetrofitClient.create(LocalApiInterface::class.java)
        mesasRepository = MesasRepository(db.mesaDao(), api)

        loadMesas()
    }

    private fun loadMesas() {
        viewModelScope.launch {
            mesasRepository.mesas.collect { lista ->
                _uiState.update { it.copy(mesas = lista) }
            }
        }
    }

    fun saveMesa(id: Int?, numero: String, localizacao: String, capacidade: String) {
        // Converter Strings para Int
        val numInt = numero.toIntOrNull()
        val capInt = capacidade.toIntOrNull()

        // Validação básica
        if (numInt == null || capInt == null || localizacao.isBlank()) {
            _uiState.update { it.copy(error = "Dados inválidos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                if (id == null) {
                    // Criar
                    mesasRepository.criarMesa(numInt, localizacao, capInt)
                    _uiState.update { it.copy(successMessage = "Mesa criada!") }
                } else {
                    // Editar
                    // A LINHA 62 DEVE ESTAR ASSIM:
                    // Ordem: id (Int), numero (Int), localizacao (String), capacidade (Int)
                    mesasRepository.editarMesa(id, numInt, localizacao, capInt)

                    _uiState.update { it.copy(successMessage = "Mesa atualizada!") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteMesa(mesa: Mesa) {
        viewModelScope.launch {
            try {
                mesasRepository.apagarMesa(mesa.id) // Assume que Mesa tem 'id' (Int)
            } catch(e: Exception) {
                _uiState.update { it.copy(error = "Erro ao apagar: ${e.message}") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}