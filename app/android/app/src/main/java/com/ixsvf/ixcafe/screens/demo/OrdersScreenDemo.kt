package com.ixsvf.ixcafe.screens.demo

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ixsvf.ixcafe.screens.components.AvailableGreen
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.OnAvailableGreen
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// --- MODELOS DE DADOS (DEMO) ---
private data class DemoCategory(val id: String, val name: String, val icon: ImageVector)
private data class DemoProduct(val id: String, val name: String, val price: Double)
private data class DemoCartItem(val product: DemoProduct, val quantity: Int)

// --- DADOS FALSOS (Constantes) ---
private val demoCategories = listOf(
    DemoCategory("bebidas", "Bebidas", Icons.Default.ShoppingCart),
    DemoCategory("cafetaria", "Cafetaria", Icons.Default.ShoppingCart),
    DemoCategory("sobremesas", "Sobremesas", Icons.Default.ShoppingCart)
)

private val demoProducts = mapOf(
    "bebidas" to listOf(
        DemoProduct("1", "Coca-Cola", 2.50),
        DemoProduct("2", "Água com Gás", 1.80),
        DemoProduct("3", "Sumo Laranja", 3.00),
        DemoProduct("4", "Cerveja Super Bock", 2.80),
        DemoProduct("5", "Vinho Tinto (copo)", 3.50),
        DemoProduct("6", "Ice Tea", 2.30)
    ),
    "cafetaria" to listOf(
        DemoProduct("7", "Café Expresso", 1.00),
        DemoProduct("8", "Meia de Leite", 1.50),
        DemoProduct("9", "Capuccino", 2.50)
    ),
    "sobremesas" to listOf(
        DemoProduct("10", "Mousse Chocolate", 3.50),
        DemoProduct("11", "Pastel de Nata", 1.20)
    )
)

// IVA (Taxa de 23%)
private const val IVA_RATE = 0.23

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreenDemo(
    tableId: String,
    onNavigateBackToTables: () -> Unit,
    onCloseAccount: () -> Unit // --- NOVO: Ação para fechar a conta ---
) {
    // --- ESTADO DO ECRÃ ---
    var selectedCategoryId by remember { mutableStateOf(demoCategories.first().id) }
    val productsToShow = demoProducts[selectedCategoryId] ?: emptyList()

    val cartItems = remember {
        mutableStateListOf(
            DemoCartItem(demoProducts["bebidas"]!!.find { it.name == "Ice Tea" }!!, 1),
            DemoCartItem(demoProducts["bebidas"]!!.find { it.name == "Vinho Tinto (copo)" }!!, 1),
            DemoCartItem(demoProducts["bebidas"]!!.find { it.name == "Sumo Laranja" }!!, 1)
        )
    }

    // --- ESTADO DO BOTTOM SHEET ---
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // --- ESTADO DERIVADO (Calculado automaticamente) ---
    val cartCount by remember { derivedStateOf { cartItems.sumOf { it.quantity } } }
    val subtotal by remember { derivedStateOf { cartItems.sumOf { it.product.price * it.quantity } } }
    val iva by remember { derivedStateOf { subtotal * IVA_RATE } }
    val total by remember { derivedStateOf { subtotal + iva } }


    // --- LÓGICA DE MANIPULAÇÃO DO CARRINHO ---
    val onAddProduct: (DemoProduct) -> Unit = { product ->
        val index = cartItems.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val oldItem = cartItems[index]
            cartItems[index] = oldItem.copy(quantity = oldItem.quantity + 1)
        } else {
            cartItems.add(DemoCartItem(product, 1))
        }
    }

    val onIncrementItem: (DemoCartItem) -> Unit = { item ->
        val index = cartItems.indexOf(item)
        if (index != -1) {
            cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    val onDecrementItem: (DemoCartItem) -> Unit = { item ->
        val index = cartItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                cartItems.removeAt(index)
            }
        }
    }

    val onRemoveItem: (DemoCartItem) -> Unit = { item ->
        cartItems.remove(item)
    }


    // --- UI ---

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            OrderSummarySheetContent(
                cartItems = cartItems,
                subtotal = subtotal,
                iva = iva,
                total = total,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                },
                onConfirmOrder = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                    println("Pedido confirmado! (API para cozinha seria chamada aqui)")
                },
                onCloseAccount = onCloseAccount, // --- NOVO: Passar a ação para o sheet ---
                onIncrement = onIncrementItem,
                onDecrement = onDecrementItem,
                onRemove = onRemoveItem
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "POS Mobile", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Mesa $tableId",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackToTables) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) Badge { Text(cartCount.toString()) }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrinho"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            if (cartCount > 0) {
                OrderBottomBar(
                    count = cartCount,
                    total = total,
                    onViewOrder = { showBottomSheet = true }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            // --- LISTA DE CATEGORIAS ---
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(demoCategories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onClick = { selectedCategoryId = category.id }
                    )
                }
            }
            VerticalSpace(16)

            // --- GRELHA DE PRODUTOS ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(productsToShow) { product ->
                    ProductCard(
                        product = product,
                        onAddClick = { onAddProduct(product) }
                    )
                }
            }
        }
    }
}

//
// --- COMPONENTES AUXILIARES DO ECRÃ DE PEDIDOS ---
//

@Composable
private fun CategoryChip(
    category: DemoCategory, isSelected: Boolean, onClick: () -> Unit
) {
    val containerColor = if (isSelected) AvailableGreen else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) OnAvailableGreen else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = containerColor, contentColor = contentColor) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = category.icon, contentDescription = null, modifier = Modifier.size(18.dp))
            HorizontalSpace(8)
            Text(text = category.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProductCard(
    product: DemoProduct, onAddClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.height(120.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxSize()) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatCurrency(product.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(AvailableGreen)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar", tint = OnAvailableGreen)
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
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            HorizontalSpace(8)
            Text(text = "$count itens", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = formatCurrency(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalSpace(16)
            Text(text = "Ver pedido", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

//
// --- COMPONENTES PARA O "PEDIDO ATUAL" (BOTTOM SHEET) ---
//

@Composable
private fun OrderSummarySheetContent(
    cartItems: List<DemoCartItem>,
    subtotal: Double,
    iva: Double,
    total: Double,
    onClose: () -> Unit,
    onConfirmOrder: () -> Unit,
    onCloseAccount: () -> Unit, // --- NOVO PARÂMETRO ---
    onIncrement: (DemoCartItem) -> Unit,
    onDecrement: (DemoCartItem) -> Unit,
    onRemove: (DemoCartItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // --- Cabeçalho do Sheet ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pedido Atual",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        VerticalSpace(16)

        // --- Lista de Itens do Carrinho ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onIncrement = { onIncrement(item) },
                    onDecrement = { onDecrement(item) },
                    onRemove = { onRemove(item) }
                )
            }
        }
        VerticalSpace(24)

        // --- Sumário de Preços ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            PriceLine(label = "Subtotal", amount = subtotal)
            VerticalSpace(8)
            PriceLine(label = "IVA (23%)", amount = iva)
            VerticalSpace(8)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            VerticalSpace(8)
            PriceLine(label = "Total", amount = total, isTotal = true)
        }

        // --- Botão de Confirmar Pedido ---
        Button(
            onClick = onConfirmOrder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // Espaço acima
            colors = ButtonDefaults.buttonColors(
                containerColor = AvailableGreen
            )
        ) {
            Text(
                text = "Confirmar Pedido",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // --- NOVO: Botão de Fechar Conta ---
        OutlinedButton(
            onClick = onCloseAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp), // Espaço entre botões e abaixo
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            Text(
                text = "Fechar Conta",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Cor do texto
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: DemoCartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Nome do item e botão de remover
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${formatCurrency(item.product.price)} cada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            VerticalSpace(8)
            // Stepper e total do item
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityStepper(
                    quantity = item.quantity,
                    onDecrement = onDecrement,
                    onIncrement = onIncrement
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatCurrency(item.product.price * item.quantity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AvailableGreen
                )
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SmallIconButton(
            onClick = onDecrement,
            icon = Icons.Default.Clear,
            enabled = quantity > 0
        )
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        SmallIconButton(
            onClick = onIncrement,
            icon = Icons.Default.Add,
            backgroundColor = AvailableGreen,
            contentColor = OnAvailableGreen
        )
    }
}

@Composable
private fun SmallIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (enabled) backgroundColor
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = contentColor,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}


@Composable
private fun PriceLine(
    label: String,
    amount: Double,
    isTotal: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatCurrency(amount),
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// Função utilitária para formatar o preço
private fun formatCurrency(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.GERMANY) // Usa €
    return format.format(price)
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Order Screen (Dark)")
@Preview(showBackground = true, name = "Order Screen (Light)", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun OrderScreenDemoPreview() {
    IxCafeTheme {
        OrderScreenDemo(
            tableId = "05",
            onNavigateBackToTables = {},
            onCloseAccount = {} // --- NOVO: Adicionado ao Preview ---
        )
    }
}

@Preview(showBackground = true, name = "Order Summary Sheet (Dark)")
@Composable
private fun OrderSummarySheetPreview() {
    val items = listOf(
        DemoCartItem(DemoProduct("1", "Ice Tea", 2.30), 1),
        DemoCartItem(DemoProduct("2", "Vinho Tinto (copo)", 3.50), 1),
        DemoCartItem(DemoProduct("3", "Sumo Laranja", 3.00), 1)
    )
    val subtotal = items.sumOf { it.product.price * it.quantity }
    val iva = subtotal * IVA_RATE
    val total = subtotal + iva

    IxCafeTheme(darkTheme = true) {
        Surface {
            OrderSummarySheetContent(
                cartItems = items,
                subtotal = subtotal,
                iva = iva,
                total = total,
                onClose = { },
                onConfirmOrder = { },
                onCloseAccount = { }, // --- NOVO: Adicionado ao Preview ---
                onIncrement = {},
                onDecrement = {},
                onRemove = {}
            )
        }
    }
}