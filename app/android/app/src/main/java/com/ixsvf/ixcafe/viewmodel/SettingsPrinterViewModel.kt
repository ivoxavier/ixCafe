package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.services.printer.PrinterHelper
import com.ixsvf.ixcafe.services.printer.PrinterPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsPrinterViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrinterPreferences(application)

    private val _uiState = MutableStateFlow(PrinterUiState())
    val uiState: StateFlow<PrinterUiState> = _uiState.asStateFlow()

    init {
        // Carregar dados salvos ao iniciar
        viewModelScope.launch {
            prefs.printerIp.collect { ip ->
                _uiState.value = _uiState.value.copy(ip = ip)
            }
        }
        viewModelScope.launch {
            prefs.printerPort.collect { port ->
                _uiState.value = _uiState.value.copy(port = port.toString())
            }
        }
    }

    fun updateIpField(newIp: String) {
        _uiState.value = _uiState.value.copy(ip = newIp)
    }

    fun updatePortField(newPort: String) {
        _uiState.value = _uiState.value.copy(port = newPort)
    }

    fun saveSettings() {
        viewModelScope.launch {
            val portInt = _uiState.value.port.toIntOrNull() ?: 9100
            prefs.savePrinterSettings(_uiState.value.ip, portInt)
            _uiState.value = _uiState.value.copy(message = "Configurações guardadas!")
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            val portInt = _uiState.value.port.toIntOrNull() ?: 9100

            val result = PrinterHelper.testPrint(_uiState.value.ip, portInt)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = if (result.isSuccess) "Sucesso: Impressora respondeu." else "Erro: ${result.exceptionOrNull()?.message}"
            )
        }
    }
}

data class PrinterUiState(
    val ip: String = "",
    val port: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)