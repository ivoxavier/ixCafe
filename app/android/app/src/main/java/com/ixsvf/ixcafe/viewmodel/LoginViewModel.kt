package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.EmpregadosProfile
import com.ixsvf.ixcafe.services.repository.EmpregadosRepository
// import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient // <-- REMOVIDO
// import com.ixsvf.ixcafe.services.repository.remote.endpoints.IxCafeApiInterface // <-- REMOVIDO
import com.ixsvf.ixcafe.services.repository.remote.FirebaseDataSource // <-- ADICIONADO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class LoginUiState(
    val isLoading: Boolean = false,
    val profiles: List<EmpregadosProfile> = emptyList(),
    val error: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Instanciar o Repositório
    private val repository: EmpregadosRepository

    // Estado da UI
    private val _uiState = MutableStateFlow(LoginUiState(isLoading = true))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        val database = (application as IxCafeApplication).database

        // --- ALTERAÇÃO AQUI ---
        // Substituímos o Retrofit pelo FirebaseDataSource
        val firebaseSource = FirebaseDataSource()

        // Passamos o firebaseSource para o repositório
        repository = EmpregadosRepository(database.empregadosDao(), firebaseSource)
        // --- FIM DA ALTERAÇÃO ---

        // 2. Iniciar observação da Base de Dados (Offline-First)
        observeLocalData()

        // 3. Tentar atualizar da API (Firebase)
        fetchProfiles()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            repository.empregados
                .catch { e ->
                    _uiState.update { it.copy(error = "Erro ao ler cache: ${e.message}") }
                }
                .collect { localProfiles ->
                    // Sempre que a DB muda (ou quando abrimos a app e lemos a cache),
                    // atualizamos a UI.
                    _uiState.update {
                        it.copy(
                            profiles = localProfiles,
                            // Se tivermos dados locais, já não estamos em "loading" crítico
                            isLoading = if (localProfiles.isNotEmpty()) false else it.isLoading
                        )
                    }
                }
        }
    }

    fun fetchProfiles() {
        viewModelScope.launch {
            // Nota: Não limpamos a lista atual, nem metemos isLoading = true agressivamente
            // para não piscar o ecrã se já tivermos dados em cache.

            try {
                repository.refreshEmpregados()
                // Sucesso! O Room vai atualizar e o 'observeLocalData' vai apanhar a mudança.
                // Apenas garantimos que o loading termina.
                _uiState.update { it.copy(isLoading = false, error = null) }

            } catch (e: IOException) {
                // Erro de rede (sem internet para aceder ao Firebase).
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // Só mostramos erro se a lista estiver vazia.
                        // Se tivermos cache, o utilizador nem nota que a net falhou.
                        error = if (it.profiles.isEmpty()) "Sem ligação à rede. A mostrar dados offline." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Erro ao sincronizar: ${e.message}")
                }
            }
        }
    }
}