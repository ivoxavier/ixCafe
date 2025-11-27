package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ixsvf.ixcafe.BuildConfig
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.model.Mesa
import com.ixsvf.ixcafe.viewmodel.TablesUiState

// Cores
private val AvailableGreen = Color(0xFF006D4F)
private val OccupiedOrange = Color(0xFFB54C00)
private val OnAvailableGreen = Color(0xFFFFFFFF)
private val OnOccupiedOrange = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
    uiState: TablesUiState,
    onTableClick: (Mesa) -> Unit,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        // ESTRUTURA SEGURA:
        // Uma Column que ocupa o ecrã todo.
        // NENHUM 'verticalScroll' aqui, pois a Grid já tem o seu próprio scroll.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. CABEÇALHO (Tamanho fixo, fica no topo) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Restaurante",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                HorizontalSpace(8)
                Text(
                    text = BuildConfig.CLIENT_NAME,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            VerticalSpace(24)

            // --- 2. LEGENDA (Tamanho fixo) ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Mapa de Mesas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                VerticalSpace(8)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LegendItem(AvailableGreen, "Livre")
                    HorizontalSpace(16)
                    LegendItem(OccupiedOrange, "Ocupada")
                }
            }
            VerticalSpace(16)

            // --- 3. ÁREA DA GRELHA (Ocupa o resto do espaço) ---
            // Usamos Box com weight(1f) para garantir que a área abaixo
            // ocupa todo o espaço restante e limita a altura da Grid.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // <--- O SEGREDO ESTÁ AQUI. Impede o erro de altura infinita.
            ) {
                if (uiState.mesas.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.mesas) { mesa ->
                            TableCardReal(mesa = mesa, onClick = { onTableClick(mesa) })
                        }
                    }
                } else if (!uiState.isLoading) {
                    // Mensagem de lista vazia (centralizada no espaço restante)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.error != null) "Erro: ${uiState.error}" else "Nenhuma mesa encontrada.",
                            color = if (uiState.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                // Se estiver loading, o PullToRefreshBox já trata do indicador no topo
            }
        }
    }
}

@Composable
fun TableCardReal(mesa: Mesa, onClick: () -> Unit) {
    val isOccupied = !mesa.status.equals("Livre", ignoreCase = true)

    val backgroundColor = if (isOccupied) OccupiedOrange else AvailableGreen
    val contentColor = if (isOccupied) OnOccupiedOrange else OnAvailableGreen

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mesa",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Text(
                    text = mesa.number,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            if (isOccupied) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = contentColor.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    HorizontalSpace(4)
                    Text(
                        text = "Ocupada",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor
                    )
                }
            } else {
                Text(
                    text = "Disponível\n${mesa.capacity} lug.",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        HorizontalSpace(4)
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}