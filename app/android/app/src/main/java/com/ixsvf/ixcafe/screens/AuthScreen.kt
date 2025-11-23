package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.ixcafe.R
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.screens.components.BackspaceButton
import com.ixsvf.ixcafe.screens.components.Numpad
import com.ixsvf.ixcafe.screens.components.PinDots
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.model.EmpregadosProfile // Confirme se é este o nome da sua classe ou UserProfile
import com.ixsvf.ixcafe.viewmodel.AuthState
import com.ixsvf.ixcafe.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    profileId: String,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()
    // O estado do utilizador atual vindo do ViewModel
    val empregadoProfile by viewModel.currentUser.collectAsState()
    // O estado do login (Sucesso/Erro)
    val loginState by viewModel.loginState.collectAsState()

    // Estado local do PIN inserido
    var pin by remember { mutableStateOf("") }
    // Erro local (baseado no estado do ViewModel)
    val isError = loginState is AuthState.Error

    // 1. Carregar o utilizador assim que o ecrã abre
    LaunchedEffect(profileId) {
        viewModel.loadUser(profileId)
    }

    // 2. Validar PIN quando atinge o tamanho máximo
    LaunchedEffect(pin) {
        if (pin.length == IxCafeConstants.DEMOCREDENTIALS.MAX_PIN_LENGTH) {
            viewModel.validatePin(pin)
            // Se falhar, limpamos o PIN após um breve momento (opcional, visualmente melhor)
            if (viewModel.loginState.value is AuthState.Error) {
                kotlinx.coroutines.delay(500)
                pin = ""
            }
        }
    }

    // 3. Reagir ao sucesso do login
    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // 4. Mostrar o ecrã se o perfil já foi carregado
    empregadoProfile?.let { profile ->
        AuthScreenContent(
            profile = profile,
            pinLength = pin.length,
            isError = isError,
            onNavigateBack = onNavigateBack,
            onNumberClick = { number ->
                if (pin.length < IxCafeConstants.DEMOCREDENTIALS.MAX_PIN_LENGTH) {
                    pin += number
                }
            },
            onBackspaceClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                }
            }
        )
    }
} // <--- A função AuthScreen fecha AQUI

// Componente UI separado e "Stateless" (Recebe tudo o que precisa para desenhar)
@Composable
fun AuthScreenContent(
    profile: EmpregadosProfile, // Ou UserProfile, conforme o seu projeto
    pinLength: Int,
    isError: Boolean,
    onNavigateBack: () -> Unit,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. CABEÇALHO ---
            VerticalSpace(height = 32)
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.lbl_profile),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            VerticalSpace(16)

            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            VerticalSpace(8)

            Text(
                text = if (isError) stringResource(R.string.lbl_wrong_pin) else stringResource(R.string.lbl_enter_pin),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            VerticalSpace(32)

            // --- 2. INDICADOR DE PIN ---
            PinDots(
                pinLength = pinLength,
                maxLength = IxCafeConstants.DEMOCREDENTIALS.MAX_PIN_LENGTH,
                isError = isError
            )

            VerticalSpace(32)

            // --- 3. TECLADO NUMÉRICO ---
            Numpad(
                onNumberClick = onNumberClick,
                onBackClick = onNavigateBack
            )

            // --- 4. BOTÃO DE APAGAR ---
            Spacer(modifier = Modifier.weight(0.5f))
            BackspaceButton(
                onClick = onBackspaceClick
            )
            VerticalSpace(height = 16)
        }
    }
}