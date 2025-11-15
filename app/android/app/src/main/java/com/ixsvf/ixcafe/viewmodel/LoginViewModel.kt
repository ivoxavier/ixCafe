package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.services.repository.model.ListaEmpregadosResponse
import com.ixsvf.ixcafe.services.repository.model.UserProfile
import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient
import com.ixsvf.ixcafe.services.repository.remote.endpoints.IxCafeApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


data class LoginUiState(
    val isLoading: Boolean = false,
    val profiles: List<UserProfile> = emptyList(),
    val error: String? = null
)


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.create(IxCafeApiInterface::class.java)

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        fetchProfiles()
    }

    fun fetchProfiles()
    {
        viewModelScope.launch{
            _uiState.update { it.copy(isLoading = true, error = null) }

            try
            {
                val apiProfiles = apiService.listarEmpregados()
            }
            catch (e: IOException)
            {
                _uiState.update {
                    it.copy(isLoading = false, error = "Sem ligação à rede. Tente novamente.")
                }
            }
            catch (e:Exception)
            {
                _uiState.update {
                    it.copy(isLoading = false, error = "Ocorreu um erro: ${e.message}")
                }
            }
        }

    }

    private fun ListaEmpregadosResponse.toUserProfile(): UserProfile {
        return UserProfile(
            name = this.nome,
            role = this.cargo
        )
    }
}