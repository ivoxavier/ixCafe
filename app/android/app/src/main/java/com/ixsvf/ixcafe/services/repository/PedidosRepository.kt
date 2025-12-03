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
import kotlinx.coroutines.flow.Flow // <-- Não se esqueça deste import
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PedidosRepository(
    private val queueOrderDao: QueueOrderDao,
    private val context: Context
) {

    // --- ESTA É A LINHA QUE FALTAVA ---
    // Observa a contagem de pedidos pendentes na base de dados local
    val pendingOrdersCount: Flow<Int> = queueOrderDao.getPendingCountFlow()

    suspend fun confirmarPedido(
        idMesa: Int,
        idEmpregado: String,
        itens: List<CartItem>
    ) {
        val pedidoItens = itens.map { item ->
            PedidoItemRequest(
                idProduto = item.product.id,
                quantidade = item.quantity,
                observacoes = item.observation
            )
        }

        val request = PedidoRequest(
            idMesa = idMesa,
            idEmpregado = idEmpregado,
            pedidos = pedidoItens
        )

        val jsonPedido = Json.encodeToString(request)

        queueOrderDao.insert(
            QueueOrderEntity(pedidoJson = jsonPedido)
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncOrdersWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}