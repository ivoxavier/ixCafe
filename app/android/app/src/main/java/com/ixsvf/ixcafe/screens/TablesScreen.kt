package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MailOutline
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
    // O PullToRefreshBox gere o gesto de "puxar para atualizar"
    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        // Column principal que organiza o layout verticalmente
        // IMPORTANTE: Não usamos .verticalScroll() aqui para evitar conflito com a Grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. CABEÇALHO (Fixo no topo) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth(), // Ocupar a largura toda
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Espalhar os elementos
            ) {
                // A. O Indicador de Sincronização (Esquerda)
                SyncStatusIndicator(count = uiState.pendingSyncCount)

                // B. O Título (Centro)
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                // C. Espaçador Invisível (Direita)
                // Isto serve apenas para equilibrar o layout e manter o título mais ou menos ao centro
                Spacer(modifier = Modifier.width(48.dp))
            }
            VerticalSpace(24)

            // --- 2. LEGENDA (Fixa) ---
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

            // --- 3. CONTEÚDO VARIÁVEL (Grelha ou Mensagem) ---
            // Usamos Box com weight(1f) para ocupar TODO o espaço restante no ecrã.
            // Isto impede o erro de "altura infinita".
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (uiState.mesas.isNotEmpty()) {
                    // A Grelha gere o seu próprio scroll dentro deste espaço
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), // 3 colunas
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.mesas) { mesa ->
                            TableCardReal(mesa = mesa, onClick = { onTableClick(mesa) })
                        }
                    }
                } else if (!uiState.isLoading) {
                    // Mensagem de lista vazia ou erro, centrada no espaço disponível
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.error != null) "Erro: ${uiState.error}" else "Nenhuma mesa encontrada.",
                            color = if (uiState.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                // Se isLoading for true, o PullToRefreshBox mostra o indicador no topo,
                // não precisamos de mostrar nada extra aqui.
            }
        }
    }
}

@Composable
fun TableCardReal(mesa: Mesa, onClick: () -> Unit) {
    // Lógica visual: Se não for "Livre" (case insensitive), é ocupada
    val isOccupied = !mesa.status.equals("Livre", ignoreCase = true)

    val backgroundColor = if (isOccupied) OccupiedOrange else AvailableGreen
    val contentColor = if (isOccupied) OnOccupiedOrange else OnAvailableGreen

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Altura fixa para uniformidade
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Topo do Cartão
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

            // Fundo do Cartão
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

// --- COMPONENTE DO INDICADOR DE SYNC ---
@Composable
fun SyncStatusIndicator(count: Int) {
    val isSyncing = count > 0
    val iconColor = if (isSyncing) Color(0xFFE65100) else Color(0xFF4CAF50) // Laranja vs Verde
    val containerColor = if (isSyncing) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSyncing) Icons.Default.MailOutline else Icons.Default.Check,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            if (isSyncing) {
                HorizontalSpace(4)
                Text(
                    text = "$count a enviar...",
                    style = MaterialTheme.typography.labelSmall,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}