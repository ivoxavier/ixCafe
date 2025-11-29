package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.ixcafe.BuildConfig
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.model.CartItem
import com.ixsvf.ixcafe.services.repository.model.Produto
import com.ixsvf.ixcafe.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Cores locais
private val AvailableGreen = Color(0xFF006D4F)
private val OnAvailableGreen = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    tableId: String,
    onNavigateBackToTables: () -> Unit,
    onCloseAccount: () -> Unit
) {
    // 1. Injetar ViewModel
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // 2. Filtrar produtos reais (vindos do Room) pela categoria selecionada
    val productsToShow = uiState.products.filter { it.categoryId == uiState.selectedCategoryId }

    // Estado do BottomSheet e Dialogs
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var editingItem by remember { mutableStateOf<CartItem?>(null) }

    // Dialog de Observação
    if (editingItem != null) {
        AddObservationDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onConfirm = { item, observation ->
                viewModel.updateObservation(item, observation)
                editingItem = null
            }
        )
    }

    // Bottom Sheet (Carrinho - Usa dados reais do uiState)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            OrderSummarySheetContent(
                cartItems = uiState.cartItems, // <-- Dados Reais
                subtotal = uiState.cartTotal,  // <-- Dados Reais
                // Recalcula totais baseados no estado real (se o VM já não o fizer)
                iva = uiState.cartTotal * 0.23, // Exemplo simples de IVA
                total = uiState.cartTotal, // Assumindo que o VM já manda com ou sem IVA conforme a regra
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                },
                onConfirmOrder = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                    viewModel.confirmOrder(tableId)
                },
                onCloseAccount = onCloseAccount,
                onIncrement = { viewModel.incrementItem(it) },
                onDecrement = { viewModel.decrementItem(it) },
                onRemove = { viewModel.removeItem(it) },
                onEditObservation = { editingItem = it }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = BuildConfig.CLIENT_NAME, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Mesa $tableId",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackToTables) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        BadgedBox(badge = {
                            // Conta itens reais
                            val count = uiState.cartItems.sumOf { it.quantity }
                            if (count > 0) Badge { Text(count.toString()) }
                        }) {
                            Icon(Icons.Default.ShoppingCart, "Carrinho")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            // --- LISTA DE CATEGORIAS (DINÂMICA) ---
            if (uiState.categories.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.categories) { catId ->
                        CategoryChip(
                            // Como na BD Produto só temos o ID da categoria, mostramos o ID.
                            // Futuramente, pode criar uma tabela Categorias para ter o nome.
                            categoryName = "Categ. $catId",
                            isSelected = catId == uiState.selectedCategoryId,
                            onClick = { viewModel.selectCategory(catId) }
                        )
                    }
                }
                VerticalSpace(16)
            } else if (!uiState.isLoading && uiState.products.isEmpty()) {
                // Se não há categorias nem produtos, mostra aviso
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Sem produtos configurados.", color = Color.Gray)
                }
            }

            // --- GRELHA DE PRODUTOS (DINÂMICA) ---
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (productsToShow.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(productsToShow) { product ->
                            ProductCard(
                                product = product,
                                onAddClick = { viewModel.addToCart(product) }
                            )
                        }
                    }
                } else if (uiState.categories.isNotEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Categoria vazia.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

// --- COMPONENTES UI (Reutilizados e limpos) ---

@Composable
private fun CategoryChip(categoryName: String, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor = if (isSelected) AvailableGreen else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) OnAvailableGreen else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = containerColor, contentColor = contentColor) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
            HorizontalSpace(8)
            Text(text = categoryName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProductCard(product: Produto, onAddClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.height(120.dp)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxSize()) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatCurrency(product.price), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                IconButton(onClick = onAddClick, modifier = Modifier.size(32.dp).clip(CircleShape).background(AvailableGreen)) {
                    Icon(Icons.Default.Add, "Adicionar", tint = OnAvailableGreen)
                }
            }
        }
    }
}

@Composable
private fun OrderBottomBar(
    count: Int, total: Double, onViewOrder: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onViewOrder() },
        color = AvailableGreen,
        contentColor = OnAvailableGreen,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            HorizontalSpace(8)
            Text(text = "$count itens", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = formatCurrency(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalSpace(16)
            Text(text = "Ver pedido", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}


// --- COMPONENTES PARA O "PEDIDO ATUAL" (BOTTOM SHEET) ---

@Composable
private fun OrderSummarySheetContent(
    cartItems: List<CartItem>,
    subtotal: Double,
    iva: Double,
    total: Double,
    onClose: () -> Unit,
    onConfirmOrder: () -> Unit,
    onCloseAccount: () -> Unit,
    onIncrement: (CartItem) -> Unit,
    onDecrement: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
    onEditObservation: (CartItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Pedido Atual", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Fechar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        VerticalSpace(16)

        // Lista
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onIncrement = { onIncrement(item) },
                    onDecrement = { onDecrement(item) },
                    onRemove = { onRemove(item) },
                    onEditObservation = { onEditObservation(item) }
                )
            }
        }
        VerticalSpace(24)

        // Totais
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            PriceLine(label = "Subtotal", amount = subtotal)
            VerticalSpace(8)
            PriceLine(label = "IVA (est.)", amount = iva) // Ajuste conforme lógica de negócio
            VerticalSpace(8)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            VerticalSpace(8)
            PriceLine(label = "Total", amount = total, isTotal = true)
        }

        // Botões
        Button(
            onClick = onConfirmOrder,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen)
        ) {
            Text("Confirmar Pedido", style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp), modifier = Modifier.padding(vertical = 8.dp))
        }

        OutlinedButton(
            onClick = onCloseAccount,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            Text("Fechar Conta", style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    onEditObservation: () -> Unit
) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${formatCurrency(item.product.price)} cada", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, "Remover", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }

            item.observation?.let {
                Text(
                    text = "Obs: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            VerticalSpace(8)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                QuantityStepper(quantity = item.quantity, onDecrement = onDecrement, onIncrement = onIncrement)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = formatCurrency(item.product.price * item.quantity), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AvailableGreen)
            }

            TextButton(onClick = onEditObservation, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalSpace(4)
                Text(text = if (item.observation == null) "Adicionar observação" else "Editar observação", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun QuantityStepper(quantity: Int, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SmallIconButton(onClick = onDecrement, icon = Icons.Default.ShoppingCart, enabled = quantity > 0)
        Text(text = quantity.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        SmallIconButton(onClick = onIncrement, icon = Icons.Default.Add, backgroundColor = AvailableGreen, contentColor = OnAvailableGreen)
    }
}

@Composable
private fun SmallIconButton(onClick: () -> Unit, icon: ImageVector, modifier: Modifier = Modifier, enabled: Boolean = true, backgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(32.dp).clip(CircleShape).background(if (enabled) backgroundColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
        colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor, disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun PriceLine(label: String, amount: Double, isTotal: Boolean = false) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = formatCurrency(amount), style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

private fun formatCurrency(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(price)
}

@Composable
private fun AddObservationDialog(item: CartItem, onDismiss: () -> Unit, onConfirm: (CartItem, String) -> Unit) {
    var observationText by remember { mutableStateOf(item.observation ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Observação para ${item.product.name}") },
        text = { OutlinedTextField(value = observationText, onValueChange = { observationText = it }, label = { Text("Nota") }, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = { onConfirm(item, observationText) }, colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen)) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}