package com.ixsvf.ixcafe.screens.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.model.UserProfile
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme

// O PIN correto para o modo de demonstração
private const val CORRECT_DEMO_PIN = "1234"
private const val MAX_PIN_LENGTH = 4

@Composable
fun AuthScreenDemo(
    profile: UserProfile,
    onCorrectPin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // --- LÓGICA DE AUTENTICAÇÃO ---
    LaunchedEffect(pin) {
        if (pin.length == MAX_PIN_LENGTH) {
            if (pin == CORRECT_DEMO_PIN) {
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
                contentDescription = "Perfil",
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
                text = if (showError) "PIN Incorreto!" else "Insira o seu PIN",
                style = MaterialTheme.typography.bodyMedium,
                color = if (showError) MaterialTheme.colorScheme.error
                // CORRIGIDO: Usa cor do tema
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            VerticalSpace(32)

            // --- 2. INDICADOR DE PIN ---
            PinDots(
                pinLength = pin.length,
                maxLength = MAX_PIN_LENGTH,
                isError = showError
            )
            VerticalSpace(32)

            // --- 3. TECLADO NUMÉRICO ---
            Numpad(
                onNumberClick = { number ->
                    if (pin.length < MAX_PIN_LENGTH) {
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

//
// --- COMPONENTES AUXILIARES ---
//

@Composable
private fun PinDots(pinLength: Int, maxLength: Int, isError: Boolean) {
    val errorColor = MaterialTheme.colorScheme.error
    // CORRIGIDO: Usa cor do tema
    val defaultColor = MaterialTheme.colorScheme.outline
    val filledColor = MaterialTheme.colorScheme.onBackground

    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        repeat(maxLength) { index ->
            val isFilled = index < pinLength
            val color = if (isError) errorColor else if (isFilled) filledColor else defaultColor

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) color else Color.Transparent)
                    .border(2.dp, color, CircleShape)
            )
        }
    }
}

@Composable
private fun Numpad(
    onNumberClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Linhas 1-9
        (1..9 step 3).forEach { start ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                (start until start + 3).forEach { number ->
                    NumberButton(
                        number = number.toString(),
                        onClick = { onNumberClick(number.toString()) }
                    )
                }
            }
        }
        // Linha "Voltar", 0, "Check"
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Botão "Voltar"
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.size(80.dp)
            ) {
                Text(
                    text = "Voltar",
                    // CORRIGIDO: Usa cor do tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Botão "0"
            NumberButton(number = "0", onClick = { onNumberClick("0") })
            // Botão "Confirmar" (desativado)
            IconButton(
                onClick = { /* Não faz nada */ },
                enabled = false,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Confirmar",
                    // CORRIGIDO: Cor desativada padrão do tema
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

@Composable
private fun NumberButton(number: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(16.dp),
        // CORRIGIDO: Cores dinâmicas do tema
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(text = number, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun BackspaceButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Clear,
            contentDescription = "Apagar",
            // CORRIGIDO: Usa cor do tema
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalSpace(8)
        // CORRIGIDO: Usa cor do tema
        Text("Apagar", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


//
// --- PREVIEW ---
//

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AuthScreenDemoPreview() {
    IxCafeTheme {
        AuthScreenDemo(
            // CORRIGIDO: O UserProfile precisa de 'id'
            profile = UserProfile(name = "Demonstração", role = "Empregado"),
            onCorrectPin = { },
            onNavigateBack = { }
        )
    }
}