package com.ixsvf.ixcafe.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Numpad(
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
fun NumberButton(number: String, onClick: () -> Unit) {
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
fun BackspaceButton(onClick: () -> Unit) {
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