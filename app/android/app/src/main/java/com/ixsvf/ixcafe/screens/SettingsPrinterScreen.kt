package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.ixcafe.viewmodel.SettingsPrinterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPrinterScreen(
    onNavigateBack: () -> Unit
) {
    // 1. Instanciamos o ViewModel
    val viewModel: SettingsPrinterViewModel = viewModel()

    // 2. Observamos o estado vindo do ViewModel (Single Source of Truth)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Impressora") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Dados da Impressora Térmica (ESC/POS)", style = MaterialTheme.typography.titleMedium)

            // --- CAMPO IP ---
            OutlinedTextField(
                value = uiState.ip, // O valor vem do ViewModel
                onValueChange = { viewModel.updateIpField(it) }, // Ação vai para o ViewModel
                label = { Text("Endereço IP (Ex: 192.168.1.200)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // --- CAMPO PORTA ---
            OutlinedTextField(
                value = uiState.port, // O valor vem do ViewModel
                onValueChange = { viewModel.updatePortField(it) }, // Ação vai para o ViewModel
                label = { Text("Porta (Padrão: 9100)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // --- MENSAGENS DE ERRO/SUCESSO ---
            uiState.message?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.contains("Erro"))
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(16.dp),
                        color = if (msg.contains("Erro"))
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {

                // --- BOTÃO TESTAR ---
                OutlinedButton(
                    onClick = { viewModel.testConnection() }, // Chama a função de teste
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Testar Conexão e Imprimir")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÃO SALVAR ---
                Button(
                    // CORREÇÃO AQUI: Chamamos saveSettings() sem argumentos
                    onClick = {
                        viewModel.saveSettings()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Definições")
                }
            }
        }
    }
}