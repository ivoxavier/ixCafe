package com.ixsvf.ixcafe.services.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ixsvf.ixcafe.services.repository.local.dao.QueueOrderDao
import com.ixsvf.ixcafe.services.repository.local.model.QueueOrderEntity
import com.ixsvf.ixcafe.services.repository.model.CartItem
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoItemRequest
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoRequest
import com.ixsvf.ixcafe.services.worker.SyncOrdersWorker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PedidosRepository(
    private val queueOrderDao: QueueOrderDao,
    private val context: Context
) {

    /**
     * Recebe o carrinho atual, converte para o formato da API,
     * guarda na base de dados local e agenda o envio.
     */
    suspend fun confirmarPedido(
        idMesa: Int,
        idEmpregado: Int,
        itens: List<CartItem>
    ) {
        // 1. Converter os itens do carrinho (Domínio) para DTOs da API
        val pedidoItens = itens.map { item ->
            PedidoItemRequest(
                idProduto = item.product.id,
                quantidade = item.quantity,
                observacoes = item.observation // Passa a observação (ou null)
            )
        }

        // 2. Criar o objeto de requisição completo
        val request = PedidoRequest(
            idMesa = idMesa,
            idEmpregado = idEmpregado,
            pedidos = pedidoItens
        )

        // 3. Serializar para JSON (String) para guardar na BD
        val jsonPedido = Json.encodeToString(request)

        // 4. Guardar na Fila Local (Room) - Garante persistência offline
        queueOrderDao.insert(
            QueueOrderEntity(pedidoJson = jsonPedido)
        )

        // 5. Agendar o WorkManager para tentar enviar
        // Define restrição: Só corre se houver internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Cria o pedido de trabalho (OneTime - corre uma vez por pedido inserido)
        val syncRequest = OneTimeWorkRequestBuilder<SyncOrdersWorker>()
            .setConstraints(constraints)
            .build()

        // Enfileira o trabalho no sistema Android
        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}