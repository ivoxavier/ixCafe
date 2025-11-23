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

// O estado da UI que o ecrã vai "ler"
data class TablesUiState(
    val isLoading: Boolean = false,
    val mesas: List<Mesa> = emptyList(),
    val error: String? = null
)

class TablesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MesasRepository

    // Estado interno (mutável) e público (imutável)
    private val _uiState = MutableStateFlow(TablesUiState(isLoading = true))
    val uiState: StateFlow<TablesUiState> = _uiState.asStateFlow()

    init {
        // 1. Injeção de Dependências Manual
        val database = (application as IxCafeApplication).database

        // Criamos o cliente Retrofit apontado para a API Local (.NET)
        val localApi = RetrofitClient.create(LocalApiInterface::class.java)

        repository = MesasRepository(database.mesaDao(), localApi)

        // 2. Começar a observar a base de dados local (Offline-First)
        observeLocalMesas()

        // 3. Tentar buscar dados frescos à API assim que o ecrã abre
        forceRefresh()
    }

    private fun observeLocalMesas() {
        viewModelScope.launch {
            repository.mesas
                .catch { e ->
                    _uiState.update { it.copy(error = "Erro local: ${e.message}") }
                }
                .collect { mesasLocais ->
                    _uiState.update {
                        it.copy(
                            mesas = mesasLocais,
                            // Se já temos mesas, não precisamos de mostrar o loading inicial "pesado"
                            // mas mantemos se a lista estiver vazia.
                            isLoading = if (mesasLocais.isNotEmpty()) false else it.isLoading
                        )
                    }
                }
        }
    }

    fun forceRefresh() {
        // Ativa o indicador de loading (para o PullToRefresh)
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // Chama a API .NET e atualiza o Room
                repository.refreshMesas()

                // Sucesso: limpa erros e loading
                _uiState.update { it.copy(isLoading = false, error = null) }

            } catch (e: Exception) {
                // Falha (ex: API em baixo ou sem Wi-Fi)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // Só mostra erro se a lista estiver vazia para não interromper o trabalho
                        error = if (it.mesas.isEmpty()) "Erro ao sincronizar: ${e.message}" else null
                    )
                }
            }
        }
    }
}