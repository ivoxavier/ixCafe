import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ixsvf.ixcafe.services.repository.model.ProductRequest
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.coroutines.launch

@Composable
fun CreateProductScreen(
    apiService: LocalApiInterface, // Idealmente isto viria via Injeção de Dependência (Hilt/Koin)
    onProductCreated: () -> Unit // Callback para voltar atrás quando acabar
) {
    val scope = rememberCoroutineScope()

    // Estados do formulário
    var name by remember { mutableStateOf("") }
    var priceString by remember { mutableStateOf("") } // Guardamos como String para facilitar a edição
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Novo Produto", style = MaterialTheme.typography.headlineMedium)

        // Campo Nome
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Preço
        OutlinedTextField(
            value = priceString,
            onValueChange = {
                // Permite apenas números, ponto e vírgula
                if (it.all { char -> char.isDigit() || char == '.' || char == ',' }) {
                    priceString = it
                }
            },
            label = { Text("Preço (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null

                    try {
                        // 1. Tratamento do preço (trocar vírgula por ponto)
                        val finalPrice = priceString.replace(",", ".").toDoubleOrNull()

                        if (name.isBlank() || finalPrice == null) {
                            errorMessage = "Preencha o nome e um preço válido."
                            isLoading = false
                            return@launch
                        }

                        // 2. Criar objeto
                        val newProduct = ProductRequest(name = name, price = finalPrice)

                        // 3. Chamada à API
                        val response = apiService.createProduct(newProduct)

                        if (response.isSuccessful) {
                            // Sucesso! Limpar campos ou sair
                            onProductCreated()
                        } else {
                            errorMessage = "Erro API: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Erro de conexão: ${e.message}"
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Salvar Produto")
            }
        }
    }
}