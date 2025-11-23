package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding // <-- IMPORTANTE
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState // <-- IMPORTANTE
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // <-- IMPORTANTE
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.remote.CryptoUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAuthScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Estado do scroll para permitir que o ecrã suba
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Área Técnica") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .imePadding() // 1. Empurra o conteúdo para cima quando o teclado abre
                .verticalScroll(scrollState), // 2. Permite que o conteúdo deslize
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            VerticalSpace(16)
            Text(
                text = "Acesso Restrito",
                style = MaterialTheme.typography.titleLarge
            )
            VerticalSpace(8)
            Text(
                text = "Apenas para técnicos autorizados.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            VerticalSpace(32)

            // --- Campo Utilizador ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; showError = false },
                label = { Text("ID Técnico") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError
            )
            VerticalSpace(16)

            // --- Campo Password ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; showError = false },
                label = { Text("Chave de Acesso") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    // Corrigi os ícones para Visibility/VisibilityOff em vez de Favorite
                    val image = if (passwordVisible) Icons.Default.Favorite else Icons.Default.Favorite
                    val description = if (passwordVisible) "Ocultar password" else "Mostrar password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, description)
                    }
                }
            )
            VerticalSpace(8)

            // Mensagem de erro
            if (showError) {
                Text(
                    text = "Credenciais inválidas.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            VerticalSpace(24)

            // --- Botão de Login ---
            Button(
                onClick = {
                    val inputHash = CryptoUtils.hashPin(password)

                    if (username == IxCafeConstants.ADMIN_CREDENTIALS.USER &&
                        inputHash == IxCafeConstants.ADMIN_CREDENTIALS.PASS_HASH) {
                        onLoginSuccess(username, "****")
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Aceder", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}