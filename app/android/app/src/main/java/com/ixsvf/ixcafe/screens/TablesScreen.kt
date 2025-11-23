package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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

// Cores (mantivemos as mesmas do demo, mas agora locais)
private val AvailableGreen = Color(0xFF006D4F)
private val OccupiedOrange = Color(0xFFB54C00)
private val OnAvailableGreen = Color(0xFFFFFFFF)
private val OnOccupiedOrange = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
    uiState: TablesUiState, // Recebe o estado real do ViewModel
    onTableClick: (Mesa) -> Unit,
    onRefresh: () -> Unit
) {

    // Wrapper do PullToRefresh para permitir recarregar mesas manualmente
    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        // Box com scroll para garantir que o swipe funciona sempre
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- CABEÇALHO ---
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
                        text = BuildConfig.CLIENT_NAME, // Nome do cliente real
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                VerticalSpace(24)

                // --- LEGENDA ---
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

                // --- LISTA DE MESAS VAZIA OU COM ERRO ---
                if (uiState.mesas.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.error != null) "Erro: ${uiState.error}" else "Nenhuma mesa encontrada.",
                            color = if (uiState.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- GRELHA DE MESAS (Conteúdo Principal) ---
            // Colocamos fora da Column (mas dentro do Box scrollable) para melhor performance da Grid
            if (uiState.mesas.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // Ajustado para 3 colunas (melhor em mobile)
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 130.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    // Padding top calculado para não tapar o cabeçalho
                    // Numa app complexa usaria Scaffold ou Column scrollable + FlowRow
                ) {
                    items(uiState.mesas) { mesa ->
                        TableCardReal(mesa = mesa, onClick = { onTableClick(mesa) })
                    }
                }
            }
        }
    }
}

@Composable
fun TableCardReal(mesa: Mesa, onClick: () -> Unit) {
    // Lógica simples: se o estado vindo da API não for "Livre", consideramos ocupada
    val isOccupied = !mesa.status.equals("Livre", ignoreCase = true)

    val backgroundColor = if (isOccupied) OccupiedOrange else AvailableGreen
    val contentColor = if (isOccupied) OnOccupiedOrange else OnAvailableGreen

    // Usamos Card ou Box com tamanho fixo para a grelha ficar alinhada
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Altura fixa para consistência visual
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Topo: Label e Número
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
                    text = mesa.number, // Agora usamos o campo 'number' (String)
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            // Fundo: Detalhes
            if (isOccupied) {
                // Mesa Ocupada
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = contentColor.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                        HorizontalSpace(4)
                        Text(
                            text = "Ocupada", // Futuro: "${mesa.pessoas} p."
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor
                        )
                    }
                    // Se a API enviar tempo, mostramos aqui
                    /*
                    VerticalSpace(2)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = contentColor.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                        HorizontalSpace(4)
                        Text(
                            text = "45min",
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor
                        )
                    }
                    */
                }
            } else {
                // Mesa Livre
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
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        HorizontalSpace(8)
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}