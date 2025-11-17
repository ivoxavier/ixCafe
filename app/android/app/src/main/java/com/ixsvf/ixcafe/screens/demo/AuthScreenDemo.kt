package com.ixsvf.ixcafe.screens.demo

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ixsvf.ixcafe.R
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.screens.components.BackspaceButton
import com.ixsvf.ixcafe.screens.components.Numpad
import com.ixsvf.ixcafe.screens.components.PinDots
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.model.UserProfile

@Composable
fun AuthScreenDemo(
    profile: UserProfile,
    onCorrectPin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(pin) {
        if (pin.length == IxCafeConstants.DEMOCREDENTIALS.MAX_PIN_LENGTH) {
            if (pin == IxCafeConstants.DEMOCREDENTIALS.DEMO_PROFILE_PIN) {
                kotlinx.coroutines.delay(200)
                onCorrectPin()
            } else {
                showError = true
                kotlinx.coroutines.delay(1000)
                pin = ""
                showError = false
            }
        }
    }

    // --- UI ---
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

            // --- 1. CABEÇALHO (ÍCONE E NOME) ---
            VerticalSpace(height = 32)
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.lbl_profile),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    // CORRIGIDO: Usa cores do tema
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                // CORRIGIDO: Usa cores do tema
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
                text = if (showError) stringResource(R.string.lbl_wrong_pin) else stringResource(R.string.lbl_enter_pin),
                style = MaterialTheme.typography.bodyMedium,
                color = if (showError) MaterialTheme.colorScheme.error
                // CORRIGIDO: Usa cor do tema
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            VerticalSpace(32)

            // --- 2. INDICADOR DE PIN ---
            PinDots(
                pinLength = pin.length,
                maxLength = IxCafeConstants.DEMOCREDENTIALS.MAX_PIN_LENGTH,
                isError = showError
            )
            VerticalSpace(32)

            // --- 3. TECLADO NUMÉRICO ---
            Numpad(
                onNumberClick = { number ->
                    if (pin.length < IxCafeConstants.DEMOCREDENTIALS.MAX_PIN_LENGTH) {
                        pin += number
                    }
                },
                onBackClick = onNavigateBack
            )
            // --- 4. BOTÃO DE APAGAR (NO FUNDO) ---
            Spacer(modifier = Modifier.weight(0.5f))
            BackspaceButton(
                onClick = {
                    if (pin.isNotEmpty()) {
                        pin = pin.dropLast(1)
                    }
                }
            )
            VerticalSpace(height = 16)
        }
    }
}