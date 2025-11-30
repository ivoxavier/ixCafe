package com.ixsvf.ixcafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.PedidosRepository
import com.ixsvf.ixcafe.services.repository.ProdutosRepository
import com.ixsvf.ixcafe.services.repository.model.CartItem
import com.ixsvf.ixcafe.services.repository.model.Categoria // <-- Importe a Categoria
import com.ixsvf.ixcafe.services.repository.model.Produto
import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderUiState(
    val isLoading: Boolean = true,
    val products: List<Produto> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    // ALTERADO: Agora guardamos a lista de objetos Categoria, não apenas IDs
    val categories: List<Categoria> = emptyList(),
    val selectedCategoryId: Int = 0,
    val cartTotal: Double = 0.0,
    val error: String? = null
)

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val produtosRepository: ProdutosRepository
    private val pedidosRepository: PedidosRepository

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        val db = (application as IxCafeApplication).database
        val api = RetrofitClient.create(LocalApiInterface::class.java)

        produtosRepository = ProdutosRepository(db.produtoDao(), db.categoriaDao(), api)
        pedidosRepository = PedidosRepository(db.queueOrderDao(), application)

        // Removemos a chamada antiga 'loadProducts()' e usamos apenas a nova
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 1. Sync API (Usando a variável correta: produtosRepository)
            try {
                produtosRepository.refreshTudo()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Observar Categorias (Usando a variável correta: produtosRepository)
            produtosRepository.categorias.collect { cats ->
                _uiState.update { state ->
                    state.copy(
                        categories = cats,
                        // Seleciona a primeira categoria por defeito se ainda não houver seleção
                        selectedCategoryId = if (state.selectedCategoryId == 0 && cats.isNotEmpty()) cats.first().id else state.selectedCategoryId,
                        // Se tivermos categorias, paramos o loading principal
                        isLoading = false
                    )
                }
            }
        }

        // 3. Observar Produtos (Usando a variável correta: produtosRepository)
        viewModelScope.launch {
            produtosRepository.todosProdutos.collect { produtos ->
                _uiState.update { it.copy(products = produtos) }
            }
        }
    }

    fun selectCategory(categoryId: Int) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    // --- LÓGICA DO CARRINHO ---

    fun addToCart(product: Produto) {
        val currentCart = _uiState.value.cartItems.toMutableList()

        // Procura item igual
        val existingItemIndex = currentCart.indexOfFirst { it.product.id == product.id && it.observation == null }

        if (existingItemIndex != -1) {
            val item = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = item.copy(quantity = item.quantity + 1)
        } else {
            currentCart.add(CartItem(product, 1))
        }
        updateCartState(currentCart)
    }

    fun incrementItem(item: CartItem) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val index = currentCart.indexOf(item)
        if (index != -1) {
            currentCart[index] = item.copy(quantity = item.quantity + 1)
            updateCartState(currentCart)
        }
    }

    fun decrementItem(item: CartItem) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val index = currentCart.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                currentCart[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentCart.removeAt(index)
            }
            updateCartState(currentCart)
        }
    }

    fun removeItem(item: CartItem) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        currentCart.remove(item)
        updateCartState(currentCart)
    }

    fun updateObservation(item: CartItem, newObservation: String) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val index = currentCart.indexOf(item)
        if (index != -1) {
            val obs = if (newObservation.isBlank()) null else newObservation
            currentCart[index] = item.copy(observation = obs)
            updateCartState(currentCart)
        }
    }

    private fun updateCartState(newCart: List<CartItem>) {
        val subtotal = newCart.sumOf { it.product.price * it.quantity }
        val total = subtotal
        _uiState.update { it.copy(cartItems = newCart, cartTotal = total) }
    }

    // --- AÇÕES FINAIS ---

    fun confirmOrder(tableIdStr: String) {
        val tableId = tableIdStr.toIntOrNull() ?: return
        val currentCart = _uiState.value.cartItems

        if (currentCart.isEmpty()) return

        viewModelScope.launch {
            try {
                val empregadoId = 1 // TODO: Ler da Sessão
                pedidosRepository.confirmarPedido(tableId, empregadoId, currentCart)
                updateCartState(emptyList())
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao registar: ${e.message}") }
            }
        }
    }
}