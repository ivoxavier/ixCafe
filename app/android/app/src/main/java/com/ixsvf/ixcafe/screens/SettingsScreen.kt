package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfileManagement: () -> Unit,
    onNavigateToTableManagement: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Definições") },
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            VerticalSpace(16) // Espaço a partir da barra do topo

            // --- Opção 1: Gestão de Perfis ---
            SettingsItem(
                icon = Icons.Default.AccountBox,
                title = "Gestão de Perfis",
                subtitle = "Criar, editar e desativar perfis de empregado",
                onClick = onNavigateToProfileManagement
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // --- Opção 2: Gestão de Mesas ---
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Gestão de Mesas",
                subtitle = "Criar, editar e eliminar mesas",
                onClick = onNavigateToTableManagement
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ... Pode adicionar mais opções de settings aqui ...
        }
    }
}

/**
 * Um item de lista reutilizável para o menu de definições.
 */
@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary // Cor de destaque
        )
        HorizontalSpace(16)

        // Textos
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalSpace(8)

        // Seta para a direita
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Settings Screen (Dark)", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, name = "Settings Screen (Light)")
@Composable
private fun SettingsScreenPreview() {
    IxCafeTheme {
        SettingsScreen(
            onNavigateBack = {},
            onNavigateToProfileManagement = {},
            onNavigateToTableManagement = {}
        )
    }
}