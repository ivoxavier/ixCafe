package com.ixsvf.ixcafe.screens.demo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ixsvf.ixcafe.screens.components.AvailableGreen
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.OccupiedOrange
import com.ixsvf.ixcafe.screens.components.OnAvailableGreen
import com.ixsvf.ixcafe.screens.components.OnOccupiedOrange
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme

// --- MODELO DE DADOS PARA A MESA (DEMO) ---
data class DemoTable(
    val id: String,
    val status: TableStatus,
    val people: Int? = null,
    val timeRemaining: String? = null
)

enum class TableStatus {
    AVAILABLE,
    OCCUPIED
}



@Composable
fun TablesScreenDemo(
    modifier: Modifier = Modifier,
    onTableClick: (DemoTable) -> Unit // Para quando quiser interagir com as mesas
) {
    val tables = rememberDemoTables()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CABEÇALHO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Restaurante",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
                HorizontalSpace(8)
                Text(
                    text = "Restaurante O Sabor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            VerticalSpace(24)

            // --- LEGENDA DO MAPA ---
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.Start),
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
                    LegendItem(color = AvailableGreen, text = "Livre")
                    HorizontalSpace(16)
                    LegendItem(color = OccupiedOrange, text = "Ocupada")
                }
            }
            VerticalSpace(16)

            // --- GRETA DE MESAS ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(4), // 4 colunas como na imagem
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tables) { table ->
                    TableCard(table = table,
                        onClick = { onTableClick(table) })
                }
            }
        }
    }
}

@Composable
fun TableCard(table: DemoTable, onClick: () -> Unit) {
    val backgroundColor = when (table.status) {
        TableStatus.AVAILABLE -> AvailableGreen
        TableStatus.OCCUPIED -> OccupiedOrange
    }
    val contentColor = when (table.status) {
        TableStatus.AVAILABLE -> OnAvailableGreen
        TableStatus.OCCUPIED -> OnOccupiedOrange
    }

    // Usamos um Box com IntrinsicSize para que a altura do card se ajuste
    // ao conteúdo, mas mantendo um mínimo
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Garante que a altura é mínima para o conteúdo
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = Modifier.fillMaxHeight()) { // Garante que a coluna preenche a altura
            Text(
                text = "Mesa",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f)
            )
            Text(
                text = table.id,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp), // Tamanho maior
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.weight(1f)) // Empurra o conteúdo para cima/baixo

            when (table.status) {
                TableStatus.AVAILABLE -> {
                    Text(
                        text = "Disponível",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
                TableStatus.OCCUPIED -> {
                    table.people?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Pessoas",
                                tint = contentColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            HorizontalSpace(4)
                            Text(
                                text = "$it pessoas",
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                    table.timeRemaining?.let {
                        VerticalSpace(4)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Tempo Restante",
                                tint = contentColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            HorizontalSpace(4)
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
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

@Composable
fun rememberDemoTables(): List<DemoTable> {
    return remember {
        listOf(
            DemoTable(id = "01", status = TableStatus.AVAILABLE),
            DemoTable(id = "02", status = TableStatus.OCCUPIED, people = 4, timeRemaining = "45min"),
            DemoTable(id = "03", status = TableStatus.AVAILABLE),
            DemoTable(id = "04", status = TableStatus.OCCUPIED, people = 2, timeRemaining = "1h\n20min"),
            DemoTable(id = "05", status = TableStatus.AVAILABLE),
            DemoTable(id = "06", status = TableStatus.AVAILABLE),
            DemoTable(id = "07", status = TableStatus.OCCUPIED, people = 6, timeRemaining = "30min"),
            DemoTable(id = "08", status = TableStatus.AVAILABLE),
            DemoTable(id = "09", status = TableStatus.OCCUPIED, people = 3, timeRemaining = "2h\n10min"),
            DemoTable(id = "10", status = TableStatus.AVAILABLE),
            DemoTable(id = "11", status = TableStatus.AVAILABLE),
            DemoTable(id = "12", status = TableStatus.OCCUPIED, people = 2, timeRemaining = "15min"),
            DemoTable(id = "13", status = TableStatus.AVAILABLE),
            DemoTable(id = "14", status = TableStatus.AVAILABLE),
            DemoTable(id = "15", status = TableStatus.OCCUPIED, people = 5, timeRemaining = "1h\n05min"),
            DemoTable(id = "16", status = TableStatus.AVAILABLE)
        )
    }
}

